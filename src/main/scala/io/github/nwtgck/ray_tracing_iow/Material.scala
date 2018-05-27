package io.github.nwtgck.ray_tracing_iow

import scala.util.Random

case class ScatterRecord(attenuation: Color3, scattered: Ray)

abstract class Material {
  def scatter(rand: Random, rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord]
}

object MaterialUtils{

  def reflect(v: Vec3, n: Vec3): Vec3 =  v - n * 2f * v.dot(n)

  def refract(v: Vec3, n: Vec3, niOverNt: Float): Option[Vec3] = {
    val uv          : Vec3  = v.unitVector
    val dt          : Float = uv.dot(n)
    val discriminant: Float = 1.0f - niOverNt * niOverNt * (1f - dt * dt)
    if(discriminant > 0f){
      Some(
        (uv - n * dt) * niOverNt - n * Math.sqrt(discriminant).toFloat
      )
    } else {
      None
    }
  }

  def schlick(cosine: Float, refIdx: Float): Float = {
    val r0: Float       = (1f - refIdx) / (1 + refIdx)
    val `r0 ^ 2`: Float = r0 * r0
    `r0 ^ 2` + (1 - `r0 ^ 2`) * Math.pow(1f - cosine, 5f).toFloat
  }
}

case class LambertMaterial(albedo: Color3) extends Material{
  override def scatter(rand: Random, rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord] = {
    val target: Vec3 = hitRecord.p + hitRecord.normal + Utils.randomInUnitSphere(rand)

    Some(ScatterRecord(
      attenuation = albedo,
      scattered   = Ray(hitRecord.p, target-hitRecord.p)
    ))
  }
}

case class MetalMaterial(albedo: Color3, f: Float) extends Material{

  val fuzz: Float = if(f < 1) f else 1

  override def scatter(rand: Random, rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord] = {

    val reflected: Vec3 = MaterialUtils.reflect(rIn.direction.unitVector, hitRecord.normal)

    val scattered: Ray = Ray(hitRecord.p, reflected + Utils.randomInUnitSphere(rand) * fuzz)

    if(scattered.direction.dot(hitRecord.normal) > 0){
      Some(ScatterRecord(
        attenuation = albedo,
        scattered   = scattered
      ))
    } else {
      None
    }
  }
}

case class DielectricMaterial(refIdx: Float) extends Material {
  override def scatter(rand: Random, rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord] = {
    val reflected: Vec3 = MaterialUtils.reflect(rIn.direction, hitRecord.normal)

    val attenuation: Color3 = Color3(1.0f, 1.0f, 1.0f)

    val (
      outwardNormal: Vec3,
      niOverNt     : Float,
      cosine       : Float
    ) =
      if(rIn.direction.dot(hitRecord.normal) > 0f){
        (
          -hitRecord.normal,
          refIdx,
          refIdx * rIn.direction.dot(hitRecord.normal) / rIn.direction.length
        )
      } else {
        (
          hitRecord.normal,
          1.0f / refIdx,
          -(rIn.direction.dot(hitRecord.normal)) / rIn.direction.length
        )
      }

    val reflectProb: Float = MaterialUtils.schlick(cosine, refIdx)

    MaterialUtils.refract(rIn.direction, outwardNormal, niOverNt) match {
      case Some(refracted) if reflectProb <= rand.nextFloat() =>
        Some(ScatterRecord(
          attenuation = attenuation,
          scattered = Ray(hitRecord.p, refracted)
        ))
      case _ => Some(ScatterRecord(
        attenuation = attenuation,
        scattered = Ray(hitRecord.p, reflected)
      ))
    }
  }
}
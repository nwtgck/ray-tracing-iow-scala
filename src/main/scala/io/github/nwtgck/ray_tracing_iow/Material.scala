package io.github.nwtgck.ray_tracing_iow

case class ScatterRecord(attenuation: Vec3, scattered: Ray)

abstract class Material {
  def scatter(rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord]
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
}

case class LambertMaterial(albedo: Vec3) extends Material{
  override def scatter(rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord] = {
    // TODO: Not to use `Main.randomInUnitSphare()`, Should move `Main.randomInUnitSphare()` to object `Utils` or etc.
    val target: Vec3 = hitRecord.p + hitRecord.normal + Main.randomInUnitSphare()

    Some(ScatterRecord(
      attenuation = albedo,
      scattered   = Ray(hitRecord.p, target-hitRecord.p)
    ))
  }
}

case class MetalMaterial(albedo: Vec3, f: Float) extends Material{

  val fuzz: Float = if(f < 1) f else 1

  override def scatter(rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord] = {

    val reflected: Vec3 = MaterialUtils.reflect(rIn.direction.unitVector, hitRecord.normal)

    // TODO: Not to use `Main.randomInUnitSphare()`, Should move `Main.randomInUnitSphare()` to object `Utils` or etc.
    val scattered: Ray = Ray(hitRecord.p, reflected + Main.randomInUnitSphare() * fuzz)

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
  override def scatter(rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord] = {
    val reflected: Vec3 = MaterialUtils.reflect(rIn.direction, hitRecord.normal)

    val attenuation: Vec3 = Vec3(1.0f, 1.0f, 1.0f)

    val (outwardNormal: Vec3, niOverNt: Float) =
      if(rIn.direction.dot(hitRecord.normal) > 0f){
        (-hitRecord.normal, refIdx)
      } else {
        (hitRecord.normal, 1.0f / refIdx)
      }

    // NOTE: You may use map instead of `match`
    MaterialUtils.refract(rIn.direction, outwardNormal, niOverNt) match {
      case Some(refracted) => Some(ScatterRecord(
        attenuation = attenuation,
        scattered = Ray(hitRecord.p, refracted)
      ))
      case None => None
    }
  }
}
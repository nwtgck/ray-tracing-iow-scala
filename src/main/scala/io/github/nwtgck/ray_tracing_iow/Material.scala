package io.github.nwtgck.ray_tracing_iow

case class ScatterRecord(attenuation: Vec3, scattered: Ray)

abstract class Material {
  def scatter(rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord]
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

  // NOTE: May extract this method
  def reflect(v: Vec3, n: Vec3): Vec3 =  v - n * 2f * v.dot(n)

  override def scatter(rIn: Ray, hitRecord: HitRecord): Option[ScatterRecord] = {

    val reflected: Vec3 = reflect(rIn.direction.unitVector, hitRecord.normal)

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
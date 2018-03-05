package io.github.nwtgck.ray_tracing_iow

case class SphereHitable(center: Vec3, radius: Float) extends Hitable {
  override def hit(r: Ray, tMin: Float, tMax: Float): Option[HitRecord] = {
    val oc: Vec3 = r.origin - center
    val a: Float = r.direction.dot(r.direction)
    val b: Float = oc.dot(r.direction)
    val c: Float = oc.dot(oc) - radius*radius
    val discriminant: Float = b * b - a * c

    if(discriminant > 0){
      val temp: Float = (-b - Math.sqrt(discriminant).toFloat) / a
      if(temp < tMax && temp > tMin){
        val t      = temp
        val p      = r.pointAtParameter(t)
        val normal = (p - center) / radius
        Some(HitRecord(
          t      = t,
          p      = p,
          normal = normal
        ))
      } else {
        val temp: Float = (-b + Math.sqrt(discriminant).toFloat) / a
        if(temp < tMax && temp > tMin){
          val t = temp
          val p = r.pointAtParameter(t)
          val normal = (p - center)/radius
          Some(HitRecord(
            t      = t,
            p      = p,
            normal = normal
          ))

        } else {
          None
        }
      }
    } else {
      None
    }
  }
}

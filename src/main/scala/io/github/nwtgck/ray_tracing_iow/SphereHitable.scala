package io.github.nwtgck.ray_tracing_iow

case class SphereHitable(center: Vec3, radius: Float) extends Hitable {
  override def hit(r: Ray, tMin: Float, tMax: Float): Option[HitRecord] = {
    val oc: Vec3 = r.origin - center
    val a: Float = r.direction.dot(r.direction)
    val b: Float = oc.dot(r.direction)
    val c: Float = oc.dot(oc) - radius*radius
    val discriminant: Float = b * b - a * c

    val temp1: Float = (-b - Math.sqrt(discriminant).toFloat) / a
    val temp2: Float = (-b + Math.sqrt(discriminant).toFloat) / a
    val b1   : Boolean = tMin < temp1 && temp1 < tMax
    val b2   : Boolean = tMin < temp2 && temp2 < tMax
    if(discriminant > 0 && (b1 || b2)){
      val t      = if(b1) temp1 else temp2
      val p      = r.pointAtParameter(t)
      val normal = (p - center) / radius
      Some(HitRecord(
        t      = t,
        p      = p,
        normal = normal
      ))
    } else {
      None
    }
  }
}

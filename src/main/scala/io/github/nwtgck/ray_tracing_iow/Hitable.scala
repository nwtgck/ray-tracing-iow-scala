package io.github.nwtgck.ray_tracing_iow

case class HitRecord(t: Float, p: Vec3, normal: Vec3, material: Material)

abstract class Hitable {
  /**
    * Hit or not
    * if hit, Some(hitable). If not, None
    * @param r
    * @param tMin
    * @param tMax
    * @return
    */
  def hit(r: Ray, tMin: Float, tMax: Float): Option[HitRecord]
}

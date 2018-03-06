package io.github.nwtgck.ray_tracing_iow

case class Camera(lookfrom: Vec3, lookat: Vec3, vup: Vec3, vfov: Float, aspect: Float) {

  // NOTE: Should hide some fields

  val theta     : Float = vfov * Math.PI.toFloat / 180f
  val halfHeight: Float = Math.tan(theta / 2f).toFloat
  val halfWidth : Float = aspect * halfHeight
  val origin    : Vec3 = lookfrom
  val w         : Vec3 = (lookfrom - lookat).unitVector
  val u         : Vec3 = vup.cross(w).unitVector
  val v         : Vec3 = w.cross(u)

  val lowerLeftCorner: Vec3 = origin - u * halfHeight -  v * halfHeight - w
  val horizontal     : Vec3 = u * 2 * halfWidth
  val vertical       : Vec3 = v * 2 * halfHeight

  def getRay(s: Float, t: Float): Ray = {
    Ray(
      origin    = origin,
      direction = lowerLeftCorner + horizontal * s + vertical * t - origin
    )
  }
}

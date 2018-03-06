package io.github.nwtgck.ray_tracing_iow

class Camera(vfov: Float, aspect: Float) {

  val theta     : Float = vfov * Math.PI.toFloat / 180f
  val halfHeight: Float = Math.tan(theta / 2f).toFloat
  val halfWidth : Float = aspect * halfHeight


  val lowerLeftCorner: Vec3 = Vec3(-halfWidth, -halfHeight, -1.0f)
  val horizontal     : Vec3 = Vec3(2f * halfWidth, 0.0f, 0.0f)
  val vertical       : Vec3 = Vec3(0.0f, 2f * halfHeight, 0.0f)
  val origin         : Vec3 = Vec3(0.0f, 0.0f, 0.0f)

  def getRay(u: Float, v: Float): Ray = {
    Ray(
      origin    = origin,
      direction = lowerLeftCorner + horizontal * u + vertical * v
    )
  }
}

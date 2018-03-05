package io.github.nwtgck.ray_tracing_iow

class Camera() {
  val lowerLeftCorner: Vec3 = Vec3(-2.0f, -1.0f, -1.0f)
  val horizontal     : Vec3 = Vec3(4.0f, 0.0f, 0.0f)
  val vertical       : Vec3 = Vec3(0.0f, 2.0f, 0.0f)
  val origin         : Vec3 = Vec3(0.0f, 0.0f, 0.0f)

  def getRay(u: Float, v: Float): Ray = {
    Ray(
      origin    = origin,
      direction = lowerLeftCorner + horizontal * u + vertical * v
    )
  }
}

package io.github.nwtgck.ray_tracing_iow

case class Ray(origin: Vec3, direction: Vec3) {
  def pointAtParameter(t: Float): Vec3 = origin + direction * t
}

package io.github.nwtgck.ray_tracing_iow

case class Vec3(x: Float, y: Float, z: Float) {

  // Length
  lazy val length: Float = Math.sqrt(squaredLength).toFloat
  // Squared length
  lazy val squaredLength: Float = x * x + y * y + z * z
  // Get unit vector
  lazy val unitVector: Vec3 = this / length

  // Addition
  def +(that: Vec3): Vec3 = Vec3(x+that.x, y+that.y, z+that.z)
  // Negation
  val unary_- : Vec3 = Vec3(-x, -y, -z)
  // Subtraction
  def -(that: Vec3): Vec3 = this + (-that)
  // Scaling (*)
  def *(t: Float): Vec3 = Vec3(t*x, t*y, t*z)
  // Scaling (/)
  def /(t: Float): Vec3 = Vec3(t/x, t/y, t/z)
  // Inner product
  def dot(that: Vec3): Float = x * that.x + y * that.y + z * that.z
  // Cross product
  def cross(that: Vec3): Vec3 = Vec3(
    y*that.z - that.y * z,
    z*that.x - that.z * x,
    x*that.y - that.x * y
  )

}
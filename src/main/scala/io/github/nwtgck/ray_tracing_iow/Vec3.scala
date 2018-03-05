package io.github.nwtgck.ray_tracing_iow

case class Vec3(x: Float, y: Float, z: Float) {

  // Length
  lazy val length: Float = Math.sqrt(squaredLength).toFloat
  // Squared length
  lazy val squaredLength: Float = x * x + y * y + z * z
  // Get unit vector
  lazy val unitVector: Vec3 = this / length

  // Addition
  @inline def +(that: Vec3): Vec3 = Vec3(x+that.x, y+that.y, z+that.z)
  // Negation
  @inline def unary_- : Vec3 = Vec3(-x, -y, -z)
  // Subtraction
  @inline def -(that: Vec3): Vec3 = this + (-that)
  // Scaling (*)
  @inline def *(t: Float): Vec3 = Vec3(x*t, y*t, z*t)
  // Scaling (/)
  @inline def /(t: Float): Vec3 = Vec3(x/t, y/t, z/t)
  // Inner product
  @inline def dot(that: Vec3): Float = x * that.x + y * that.y + z * that.z
  // Cross product
  @inline def cross(that: Vec3): Vec3 = Vec3(
    y*that.z - that.y * z,
    z*that.x - that.z * x,
    x*that.y - that.x * y
  )

}
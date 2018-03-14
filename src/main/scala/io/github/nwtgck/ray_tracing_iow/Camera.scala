package io.github.nwtgck.ray_tracing_iow

import scala.util.Random

object CameraUtils{
  def randomInUnitDisk(rand: Random): Vec3 =
    Stream.continually(
      Vec3(rand.nextFloat(), rand.nextFloat(), 0f) * 2.0f - Vec3(1f, 1f, 0f)
    ).find(p => p.dot(p) < 1.0f)
     .get // NOTE: `get` is logically safe
}

case class Camera(lookfrom: Vec3, lookat: Vec3, vup: Vec3, vfov: Float, aspect: Float, aperture: Float, focusDist: Float) {

  // NOTE: Should hide some fields

  val lensRadius: Float = aperture / 2f
  val theta     : Float = vfov * Math.PI.toFloat / 180f
  val halfHeight: Float = Math.tan(theta / 2f).toFloat
  val halfWidth : Float = aspect * halfHeight
  val origin    : Vec3 = lookfrom
  val w         : Vec3 = (lookfrom - lookat).unitVector
  val u         : Vec3 = vup.cross(w).unitVector
  val v         : Vec3 = w.cross(u)

  val lowerLeftCorner: Vec3 = origin - u * halfWidth * focusDist - v * halfHeight * focusDist - w * focusDist
  val horizontal     : Vec3 = u * 2 * halfWidth * focusDist
  val vertical       : Vec3 = v * 2 * halfHeight * focusDist

  def getRay(rand: Random, s: Float, t: Float): Ray = {
    val rd    : Vec3 = CameraUtils.randomInUnitDisk(rand) * lensRadius
    val offset: Vec3 = u * rd.x + v * rd.y
    Ray(
      origin    = origin + offset,
      direction = lowerLeftCorner + horizontal * s + vertical * t - origin - offset
    )
  }
}

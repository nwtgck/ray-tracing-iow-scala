package io.github.nwtgck.ray_tracing_iow

import java.io.{FileOutputStream, PrintStream}

import scala.util.Try

object Main {

  def hitSphere(center: Vec3, radius: Float, r: Ray): Float = {
    val oc: Vec3 = r.origin - center
    val a: Float = r.direction.dot(r.direction)
    val b: Float = 2.0f * oc.dot(r.direction)
    val c: Float = oc.dot(oc) - radius*radius
    val discriminant: Float = b * b - 4 * a * c

    if(discriminant < 0){
      -1.0f
    } else {
      (-b - Math.sqrt(discriminant).toFloat) / (2.0f * a)
    }
  }

  def color(r: Ray): Color3 = {
    val t: Float = hitSphere(Vec3(0f, 0f, -1f), radius = 0.5f, r)

    if(t > 0.0f){
      val N: Vec3 = (r.pointAtParameter(t) - Vec3(0f, 0f, -1f)).unitVector
      Color3(N.x+1f, N.y+1f, N.z+1f) * 0.5f
    } else {
      val unitDirection: Vec3  = r.direction.unitVector
      val t            : Float = 0.5f * (unitDirection.y + 1.0f)
      Color3(1f, 1f, 1f) * (1f - t) + Color3(0.5f, 0.7f, 1.0f) * t
    }
  }

  def main(args: Array[String]): Unit = {

    // output
    val out: PrintStream =
      Try(new PrintStream(new FileOutputStream(args(0))))
      .getOrElse(System.out)

    val nx: Int = 200
    val ny: Int = 100

    out.println(
      s"""P3
         |${nx} ${ny}
         |255""".stripMargin)

    val lowerLeftCorner: Vec3 = Vec3(-2.0f, -1.0f, -1.0f)
    val horizontal     : Vec3 = Vec3(4.0f, 0.0f, 0.0f)
    val vertical       : Vec3 = Vec3(0.0f, 2.0f, 0.0f)
    val origin         : Vec3 = Vec3(0.0f, 0.0f, 0.0f)

    for{
      j <- ny - 1 to 0 by -1
      i <- 0 until nx
    } {

      val u: Float = i.toFloat / nx
      val v: Float = j.toFloat / ny
      val r: Ray   = Ray(origin, lowerLeftCorner + horizontal * u + vertical * v)

      val col: Color3 = color(r)
      out.println(s"${col.ir} ${col.ig} ${col.ib}")
    }

    out.close()
  }
}

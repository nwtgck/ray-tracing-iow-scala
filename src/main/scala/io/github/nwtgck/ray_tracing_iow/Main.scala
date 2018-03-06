package io.github.nwtgck.ray_tracing_iow

import java.io.{FileOutputStream, PrintStream}

import scala.util.{Random, Try}

object Main {

  val rand: Random = new Random(seed=101)


  def randomInUnitSphare(): Vec3 = {
    // TODO: Make it declarative
    var p: Vec3 = Vec3(0f, 0f, 0f)
    do {
      p = Vec3(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()) * 2.0f - Vec3(1f, 1f, 1f)
    } while(p.squaredLength >= 1.0f)
    p
  }

  def color(r: Ray, hitable: Hitable, depth: Int): Color3 = {

    hitable.hit(r, 0.001f, Float.MaxValue) match {
      case Some(hitRecord) =>
        if(depth < 50){
          hitRecord.material.scatter(r, hitRecord) match {
            case Some(ScatterRecord(attenuation, scattered)) =>
              val col = color(scattered, hitable, depth+1)
              Color3(col.r * attenuation.x, col.g * attenuation.y, col.b * attenuation.z)
            case None =>
              Color3(0f, 0f, 0f)
          }
        } else {
          Color3(0f, 0f, 0f)
        }

      case None => {
        val unitDirection: Vec3  = r.direction.unitVector
        val t            : Float = 0.5f * (unitDirection.y + 1.0f)
        Color3(1f, 1f, 1f) * (1f - t) + Color3(0.5f, 0.7f, 1.0f) * t
      }
    }
  }

  def main(args: Array[String]): Unit = {

    // output
    val out: PrintStream =
      Try(new PrintStream(new FileOutputStream(args(0))))
      .getOrElse(System.out)

    val nx: Int = 200
    val ny: Int = 100
    val ns: Int = 100

    out.println(
      s"""P3
         |${nx} ${ny}
         |255""".stripMargin)

    val hitable        : ListHitable = ListHitable(
      SphereHitable(
        center   = Vec3(0f, 0f, -1f),
        radius   = 0.5f,
        material = LambertMaterial(albedo = Vec3(0.1f, 0.2f, 0.5f))
      ),
      SphereHitable(
        center   = Vec3(0f, -100.5f, -1f),
        radius   = 100f,
        material = LambertMaterial(albedo = Vec3(0.8f, 0.8f, 0.0f))
      ),
      SphereHitable(
        center   = Vec3(1f, 0f, -1f),
        radius   = 0.5f,
        material = MetalMaterial(albedo = Vec3(0.8f, 0.6f, 0.2f), f = 0.0f)
      ),
      SphereHitable(
        center   = Vec3(-1f, 0f, -1f),
        radius   = 0.5f,
        material = DielectricMaterial(refIdx = 1.5f)
      )
    )

    val camera: Camera = new Camera()

    for{
      j <- ny - 1 to 0 by -1
      i <- 0 until nx
    } {
      // TODO: Make it declarative
      var col: Color3 = Color3(0f, 0f, 0f)
      for(s <- 0 until ns){
        val u: Float = (i + rand.nextFloat()) / nx
        val v: Float = (j + rand.nextFloat()) / ny
        val r: Ray   = camera.getRay(u, v)
        col = col + color(r, hitable, 0)
      }
      col = col / ns.toFloat
      col = Color3(Math.sqrt(col.r).toFloat, Math.sqrt(col.g).toFloat, Math.sqrt(col.b).toFloat)
      out.println(s"${col.ir} ${col.ig} ${col.ib}")
    }

    out.close()
  }
}

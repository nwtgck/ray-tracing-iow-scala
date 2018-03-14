package io.github.nwtgck.ray_tracing_iow

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}

import org.scalatest.{FunSuite, Matchers}

import scala.util.Random

class MainTest extends FunSuite with Matchers{

  test("Main.renderToOutputStream test") {
    val rand = new Random(101)
    val byteOutputStream = new ByteArrayOutputStream()
    Main.renderToOutputStream(
      rand,
      RayTracingIOWOptions(
        width          = 150,
        height         = 100,
        ns             = 10,
        outfilePathOpt = None,
        outImgExtension = PPMImgExtension
      ),
      byteOutputStream
    )
    val actual: Array[Byte] = byteOutputStream.toByteArray
    val inputStream: InputStream = this.getClass.getClassLoader.getResourceAsStream("150x100.ppm")
    println(inputStream)
    // (from: https://stackoverflow.com/a/4905770/2885946)
    val expect     : Array[Byte] = Stream.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray

    actual shouldBe expect
  }

}

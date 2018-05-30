package io.github.nwtgck.ray_tracing_iow

import java.io.{ByteArrayOutputStream, InputStream}

import org.scalatest.{FunSuite, Matchers}

class MainTest extends FunSuite with Matchers{

  test("Main.renderToOutputStream test") {
    val byteOutputStream = new ByteArrayOutputStream()
    val width: Int = 150
    val height:Int = 100
    Utils.renderToOutputStream(
      RayTracingIOWOptions(
        width           = width,
        height          = height,
        minFloat        = 0.001f,
        nSamples        = 10,
        randomSeed      = 101,
        outfilePathOpt  = None,
        mode            = ImageMode,
        animeSkipStep   = 3,
        animeDt         = 0.01f,
        animeTMin       = 0.0f,
        animeTMax       = 4.2f,
        animeOutDirPath = "anime_out",
        imgFormat       = TextPpmImgFormat
      ),
      SceneGenerators.defaultSceneGenerator(width, height),
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

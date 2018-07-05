package io.github.nwtgck.ray_tracing_iow

import java.awt.image.BufferedImage
import java.io._

import javax.imageio.ImageIO

import scala.collection.parallel.immutable.ParSeq
import scala.util.Random

object Utils {

  def randomInUnitSphere(rand: Random): Vec3 =
    Stream.continually(
      Vec3(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()) * 2.0f - Vec3(1f, 1f, 1f)
    ).find(_.squaredLength < 1.0f)
      .get // NOTE: `get` is logically safe

  def color(rand: Random, r: Ray, hitable: Hitable, minFloat: Float, depth: Int): Color3 = {

    hitable.hit(r, minFloat, Float.MaxValue) match {
      case Some(hitRecord) =>
        if(depth < 50){
          hitRecord.material.scatter(rand, r, hitRecord) match {
            case Some(ScatterRecord(attenuation, scattered)) =>
              val col = color(rand, scattered, hitable, minFloat, depth+1)
              Color3(col.r * attenuation.r, col.g * attenuation.g, col.b * attenuation.b)
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

  def renderToOutputStream(options: RayTracingIOWOptions, sceneGenerator: Random => Scene, outputStream: OutputStream): Unit = {

    val width  : Int = options.width
    val height : Int = options.height
    val minFloat : Float = options.minFloat
    val ns     : Int = options.nSamples
    val imgFormat: ImgFormat = options.imgFormat

    val rand: Random = new Random(options.randomSeed)

    val scene: Scene = sceneGenerator(rand)
    val hitable: Hitable = scene.hitable
    val camera = scene.camera

    // Pairs of Position and Seed
    val posAndSeeds: Stream[((Int, Int), Int)] = for{
      j <- (height - 1 to 0 by -1).toStream
      i <- (0 until width).toStream
      seed = rand.nextInt()
    } yield ((i, j), seed)

    val colorAndPosPar: ParSeq[(Color3, (Int, Int))] = for{
      ((i, j), seed) <- posAndSeeds.par
    } yield {
      // TODO: Make it declarative

      val rand: Random = new Random(seed = seed)

      // Create random seeds (this is for reproducibility)
      val randomSeeds: Seq[Int] = (0 until ns).map(_ => rand.nextInt())

      var col: Color3 =
        (for(seed <- randomSeeds.par)
          yield {
            val rand: Random = new Random(seed)
            val u: Float = (i + rand.nextFloat()) / width
            val v: Float = (j + rand.nextFloat()) / height
            val r: Ray   = camera.getRay(rand, u, v)
            color(rand, r, hitable, minFloat, 0)
          }
        ).reduce(_ + _)
      col = col / ns.toFloat
      col = Color3(Math.sqrt(col.r).toFloat, Math.sqrt(col.g).toFloat, Math.sqrt(col.b).toFloat)
      (col, (i, j))
    }

    imgFormat match {
      case TextPpmImgFormat =>
        val out = new PrintStream(outputStream)
        out.println(
          s"""P3
             |${width} ${height}
             |255""".stripMargin)

        for((col, pos) <- colorAndPosPar.toStream){ // NOTE: toStream is necessary to be sure that colors are ordered
          out.println(s"${col.ir} ${col.ig} ${col.ib}")
        }
      case BinaryPpmImgFormat =>
        outputStream.write(s"""P6
                              |${width} ${height}
                              |255
                              |""".stripMargin.getBytes)
        for((col, _) <- colorAndPosPar.toStream){ // NOTE: toStream is necessary to be sure that colors are ordered
          outputStream.write(col.ir.toByte)
          outputStream.write(col.ig.toByte)
          outputStream.write(col.ib.toByte)
        }
      case _ =>
        val image: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for((col, (x, y)) <- colorAndPosPar){
          image.setRGB(x, height -1 - y, col.rgbInt)
        }
        ImageIO.write(image, imgFormat.extName, outputStream)
    }
  }


  def renderAmimeToDir(options: RayTracingIOWOptions, animeGenerator: Random => Seq[Scene]): Unit = {

    val dirPath = options.animeOutDirPath

    if(!new File(dirPath).exists()){
      new File(dirPath).mkdirs()
    }

    val rand: Random = new Random(options.randomSeed)

    val scenes: Seq[Scene] = animeGenerator(rand)


    for((scene, idx) <- scenes.zipWithIndex.par){
      val filePath: String = f"${dirPath}${File.separator}anime$idx%08d.${options.imgFormat.extName}"
      val outputStream = new BufferedOutputStream(new FileOutputStream(filePath))
      // Render to the file
      renderToOutputStream(options, sceneGenerator = (_: Random) => scene, outputStream=outputStream)
      // Close the output stream
      outputStream.close()
    }
  }

  def skipAnimeGenerator(skipStep: Int, animeGenerator: Random => Seq[Scene]): Random => Seq[Scene] = {
    (rand: Random) => {
      for{
        (h, idx) <- animeGenerator(rand).zipWithIndex
        if idx % skipStep == 0
      } yield h
    }
  }

}

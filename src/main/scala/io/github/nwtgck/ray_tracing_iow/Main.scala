package io.github.nwtgck.ray_tracing_iow

import java.awt.image.BufferedImage
import java.io.{FileOutputStream, OutputStream, PrintStream}
import javax.imageio.ImageIO

import scala.collection.parallel.immutable.ParSeq
import scala.util.Random

case class RayTracingIOWOptions(width: Int,
                                height: Int,
                                nSamples: Int,
                                minFloat: Float,
                                randomSeed: Int,
                                outfilePathOpt: Option[String],
                                outImgExtension: ImgExtension)

sealed class ImgExtension(val name: String)
case object PPMImgExtension extends ImgExtension("ppm")
case object PNGImgExtension extends ImgExtension("png")
case object JPGImgExtension extends ImgExtension("jpg")
case object GifImgExtension extends ImgExtension("gif")

object Main {

  def randomInUnitSphere(rand: Random): Vec3 =
    Stream.continually(
      Vec3(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()) * 2.0f - Vec3(1f, 1f, 1f)
    ).find(_.squaredLength >= 1.0f)
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

  def randomScene(rand: Random): Hitable = {

    // TODO: Make it declarative

    var hittables: List[Hitable] = Nil

    hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
      center   = Vec3(0f, -1000f, 0f),
      radius   = 1000f,
      material = LambertMaterial(albedo = Color3(0.5f, 0.5f, 0.5f))
    )
    for{
      a <- -11 until 11
      b <- -11 until 11
    }{
      val chooseMat: Float = rand.nextFloat()
      val center   : Vec3  = Vec3(
        a + 0.9f * rand.nextFloat(),
        0.2f,
        b + 0.9f * rand.nextFloat()
      )
      if((center - Vec3(4f, 0.2f, 0f)).length > 0.9f){
        if(chooseMat < 0.8f){ // diffuse
          hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
            center   = center,
            radius   =  0.2f,
            material = LambertMaterial(
              albedo = Color3(
                rand.nextFloat() * rand.nextFloat(),
                rand.nextFloat() * rand.nextFloat(),
                rand.nextFloat() * rand.nextFloat()
              )
            )
          )
        } else if (chooseMat < 0.95f){ // metal
          hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
            center   = center,
            radius   = 0.2f,
            material = MetalMaterial(
              albedo = Color3(
                0.5f * (1 + rand.nextFloat()),
                0.5f * (1 + rand.nextFloat()),
                0.5f * (1 + rand.nextFloat())
              ),
              f      = 0.5f * rand.nextFloat()
            )
          )
        } else {
          hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
            center   = center,
            radius   = 0.2f,
            material = DielectricMaterial(refIdx = 1.5f)
          )
        }
      }
    }

    hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
      center   = Vec3(0f, 1f, 0f),
      radius   = 1.0f,
      material = DielectricMaterial(
        refIdx = 1.5f
      )
    )
    hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
      center   = Vec3(-4f, 1f, 0f),
      radius   =  1.0f,
      material = LambertMaterial(
        albedo = Color3(0.4f, 0.2f, 0.1f)
      )
    )
    hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
      center   = Vec3(4f, 1f, 0f),
      radius   = 1.0f,
      material = MetalMaterial(
        albedo = Color3(0.7f, 0.6f, 0.5f),
        f      = 0.0f
      )
    )

    ListHitable(hittables: _*)
  }

  def renderToOutputStream(options: RayTracingIOWOptions, outputStream: OutputStream): Unit = {

    val width  : Int = options.width
    val height : Int = options.height
    val minFloat : Float = options.minFloat
    val ns     : Int = options.nSamples
    val imgExt: ImgExtension = options.outImgExtension

    val rand: Random = new Random(options.randomSeed)

    val hitable        : Hitable = randomScene(rand)

    val lookfrom: Vec3 = Vec3(13f, 2f, 3f)
    val lookat  : Vec3 = Vec3(0f, 0f, 0f)
    val focusDist: Float = 10.0f
    val aperture   : Float = 0.1f
    val camera: Camera = Camera(
      lookfrom  = lookfrom,
      lookat    = lookat,
      vup       = Vec3(0f, 1f, 0f),
      vfov      = 20f,
      aspect    = width.toFloat / height,
      aperture  = aperture,
      focusDist = focusDist
    )

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

      var col: Color3 = Color3(0f, 0f, 0f)
      for(s <- 0 until ns){
        val u: Float = (i + rand.nextFloat()) / width
        val v: Float = (j + rand.nextFloat()) / height
        val r: Ray   = camera.getRay(rand, u, v)
        col = col + color(rand, r, hitable, minFloat, 0)
      }
      col = col / ns.toFloat
      col = Color3(Math.sqrt(col.r).toFloat, Math.sqrt(col.g).toFloat, Math.sqrt(col.b).toFloat)
      (col, (i, j))
    }

    imgExt match {
      case PPMImgExtension =>
        val out = new PrintStream(outputStream)
        out.println(
          s"""P3
             |${width} ${height}
             |255""".stripMargin)

        for((col, pos) <- colorAndPosPar.toStream){ // NOTE: toStream is necessary to be sure that colors are ordered
          out.println(s"${col.ir} ${col.ig} ${col.ib}")
        }
      case _ =>
        val image: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for((col, (x, y)) <- colorAndPosPar){
          image.setRGB(x, height -1 - y, col.rgbInt)
        }
        ImageIO.write(image, imgExt.name, outputStream)
    }
  }

  def main(args: Array[String]): Unit = {

    val defaultOpts: RayTracingIOWOptions =
      RayTracingIOWOptions(
        width          = 600,
        height         = 400,
        minFloat       = 0.001f,
        randomSeed     = 101,
        nSamples       = 10,
        outfilePathOpt = None,
        outImgExtension = PPMImgExtension
      )

    val parser = new scopt.OptionParser[RayTracingIOWOptions]("Ray Tracing in One Weekend Written in Scala") {
      opt[Int]("width") action { (v, opts) =>
        opts.copy(width = v)
      } text s"width (default: ${defaultOpts.width})"

      opt[Int]("height") action { (v, opts) =>
        opts.copy(height = v)
      } text s"height (default: ${defaultOpts.height})"

      opt[Double]("min-float") action { (v, opts) =>
        opts.copy(minFloat = v.toFloat)
      } text s"min-float (default: ${defaultOpts.minFloat})"

      opt[Int]("n-samples") action { (v, opts) =>
        opts.copy(nSamples = v)
      } text s"n-samples (default: ${defaultOpts.nSamples})"

      opt[Int]("random-seed") action { (v, opts) =>
        opts.copy(randomSeed = v)
      } text s"random-seed (default: ${defaultOpts.randomSeed})"

      opt[String]("out-extension") action { (v, opts) =>
        opts.copy(
          outImgExtension = v match {
            case PPMImgExtension.name => PPMImgExtension
            case PNGImgExtension.name => PNGImgExtension
            case JPGImgExtension.name => JPGImgExtension
            case GifImgExtension.name => GifImgExtension
          }
        )
      } text s"extension of output file (default: ${defaultOpts.outImgExtension.name})"

      opt[String]("out-file") action { (v, opts) =>
        opts.copy(outfilePathOpt = Some(v))
      } text "path of output file (default: stdout)"
    }

    parser.parse(args, defaultOpts) match {
      case Some(options) =>
        // output
        val outputStream: OutputStream =
          options.outfilePathOpt.map{path => new PrintStream(new FileOutputStream(path))}
            .getOrElse(System.out)

        // Render ray-tracing image to the output stream
        renderToOutputStream(options, outputStream)

        // Close the output stream
        outputStream.close()
      case None =>
        ()
    }
  }
}

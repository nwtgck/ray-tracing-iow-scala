package io.github.nwtgck.ray_tracing_iow

import java.io.{FileOutputStream, OutputStream, PrintStream}

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
        Utils.renderToOutputStream(options, Hitables.defaultHitableGenerator, outputStream)

        // Close the output stream
        outputStream.close()
      case None =>
        ()
    }
  }
}

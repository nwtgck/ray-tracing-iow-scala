package io.github.nwtgck.ray_tracing_iow

import java.io.{FileOutputStream, OutputStream, PrintStream}

case class RayTracingIOWOptions(width: Int,
                                height: Int,
                                nSamples: Int,
                                minFloat: Float,
                                randomSeed: Int,
                                outfilePathOpt: Option[String],
                                mode: Mode,
                                animeSkipStep: Int,
                                animeTMax: Float,
                                animeOutDirPath: String,
                                imgFormat: ImgFormat)

sealed abstract class ImgFormat(val code: String, val extName: String)
case object TextPpmImgFormat   extends ImgFormat(code = "text-ppm", extName = "ppm")
case object BinaryPpmImgFormat extends ImgFormat(code = "binary-ppm", extName = "ppm")
case object PngImgFormat       extends ImgFormat(code = "png", extName = "png")
case object JpgImgFormat       extends ImgFormat(code = "jpg", extName = "jpg")
case object GifImgFormat       extends ImgFormat(code = "gif", extName = "gif")


sealed abstract class Mode(override val toString: String)
case object ImageMode extends Mode("image")
case object AnimeMode extends Mode("anime")

object Main {

  def main(args: Array[String]): Unit = {

    val defaultOpts: RayTracingIOWOptions =
      RayTracingIOWOptions(
        width           = 600,
        height          = 400,
        minFloat        = 0.001f,
        randomSeed      = 101,
        nSamples        = 10,
        outfilePathOpt  = None,
        mode            = ImageMode,
        animeSkipStep   = 3,
        animeTMax       = 6.0f,
        animeOutDirPath = "anime_out",
        imgFormat       = TextPpmImgFormat
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

      opt[String]("mode") action { (v, opts) =>
        val mode = v match {
          case ImageMode.toString =>
            ImageMode
          case AnimeMode.toString =>
            AnimeMode
        }
        opts.copy(mode = mode)
      } text s"mode - ${ImageMode} or ${AnimeMode} (default: ${defaultOpts.mode})"

      opt[Int]("anime-skip-step") action { (v, opts) =>
        opts.copy(animeSkipStep = v)
      } text s"anime-skip-step (default: ${defaultOpts.animeSkipStep})"

      opt[Double]("anime-t-max") action { (v, opts) =>
        opts.copy(animeTMax = v.toFloat)
      } text s"anime-t-max (default: ${defaultOpts.animeTMax})"

      opt[String]("anime-out-dir-path") action { (v, opts) =>
        opts.copy(animeOutDirPath = v)
      } text s"directory path of output anime images (default: ${defaultOpts.animeOutDirPath})"

      opt[String]("img-format") action { (v, opts) =>
        opts.copy(
          imgFormat = v match {
            case TextPpmImgFormat.code   => TextPpmImgFormat
            case BinaryPpmImgFormat.code => BinaryPpmImgFormat
            case PngImgFormat.code       => PngImgFormat
            case JpgImgFormat.code       => JpgImgFormat
            case GifImgFormat.code       => GifImgFormat
          }
        )
      } text s"output image format (default: ${defaultOpts.imgFormat.code})"


      opt[String]("out-file") action { (v, opts) =>
        opts.copy(outfilePathOpt = Some(v))
      } text "path of output file (default: stdout)"
    }

    parser.parse(args, defaultOpts) match {
      case Some(options) =>
        options.mode match {
          case ImageMode =>
            // output
            val outputStream: OutputStream =
              options.outfilePathOpt.map{path => new PrintStream(new FileOutputStream(path))}
                .getOrElse(System.out)
            // Render ray-tracing image to the output stream
            Utils.renderToOutputStream(options, Hitables.defaultHitableGenerator, outputStream)
            // Close the output stream
            outputStream.close()
          case AnimeMode =>
            // Save images to directory
            Utils.renderAmimeToDir(
              options,
              Utils.skipAnimeGenerator(
                skipStep       = options.animeSkipStep,
                animeGenerator = Hitables.defaultAnimationGenerator(maxT = options.animeTMax)
              )
            )
        }

      case None =>
        ()
    }
  }
}

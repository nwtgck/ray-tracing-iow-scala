# ray-tracing-iow
[![Build Status](https://travis-ci.org/nwtgck/ray-tracing-iow-scala.svg?branch=master)](https://travis-ci.org/nwtgck/ray-tracing-iow-scala)

[Ray Tracing in One Weekend](http://in1weekend.blogspot.jp/2016/01/ray-tracing-in-one-weekend.html) Written in Scala

![Animation](demo_images/camera_move_anime_lossy_100.gif)

## Run

Here is how to generate an image.

```bash
./make_jar.sh
./run_jar.sh > out.ppm
```

or

```bash
./make_jar.sh
./run_jar.sh --out-file=out.ppm
```

or

```bash
sbt "run --out-file=out.ppm"
```

Then you will get `out.ppm` image


### Options

You can specify parameters by the options.

```txt
Usage: Ray Tracing in One Weekend Written in Scala [options]

  --width <value>          width (default: 600)
  --height <value>         height (default: 400)
  --min-float <value>      min-float (default: 0.001)
  --n-samples <value>      n-samples (default: 10)
  --random-seed <value>    random-seed (default: 101)
  --mode <value>           mode - image or anime (default: image)
  --anime-skip-step <value>
                           anime-skip-step (default: 3)
  --anime-dt <value>       delta t (default: 0.01)
  --anime-t-min <value>    anime-t-min (default: 0.0)
  --anime-t-max <value>    anime-t-max (default: 6.0)
  --anime-out-dir-path <value>
                           directory path of output anime images (default: anime_out)
  --img-format <value>     output image format (default: text-ppm)
  --out-file <value>       path of output file (default: stdout)
```


## Change History

Here is the history of this repository. You can walk through the history

<!-- AUTO GEN by autogen_scripts/gen_history_md.rb -->
|Tag|Files at that point|Diff|Generated image|
|---|---|---|---|
|`chapter1`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter1) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/a8adeebcff16a343c62f2d318e263decd688ed28...chapter1)|[chapter1.png](image_logs/pngs/chapter1.png)|
|`chapter2`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter2) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter1...chapter2)|[chapter2.png](image_logs/pngs/chapter2.png)|
|`chapter3`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter3) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter2...chapter3)|[chapter3.png](image_logs/pngs/chapter3.png)|
|`chapter4`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter4) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter3...chapter4)|[chapter4.png](image_logs/pngs/chapter4.png)|
|`chapter5-1`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter5-1) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter4...chapter5-1)|[chapter5-1.png](image_logs/pngs/chapter5-1.png)|
|`chapter5-2`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter5-2) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter5-1...chapter5-2)|[chapter5-2.png](image_logs/pngs/chapter5-2.png)|
|`chapter6`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter6) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter5-2...chapter6)|[chapter6.png](image_logs/pngs/chapter6.png)|
|`chapter7-1`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter7-1) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter6...chapter7-1)|[chapter7-1.png](image_logs/pngs/chapter7-1.png)|
|`chapter7-2`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter7-2) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter7-1...chapter7-2)|[chapter7-2.png](image_logs/pngs/chapter7-2.png)|
|`chapter7-shadow-acne`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter7-shadow-acne) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter7-2...chapter7-shadow-acne)|[chapter7-shadow-acne.png](image_logs/pngs/chapter7-shadow-acne.png)|
|`chapter8-1`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter8-1) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter7-shadow-acne...chapter8-1)|[chapter8-1.png](image_logs/pngs/chapter8-1.png)|
|`chapter8-2`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter8-2) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter8-1...chapter8-2)|[chapter8-2.png](image_logs/pngs/chapter8-2.png)|
|`chapter9-2`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter9-2) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter8-2...chapter9-2)|[chapter9-2.png](image_logs/pngs/chapter9-2.png)|
|`chapter9-3`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter9-3) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter9-2...chapter9-3)|[chapter9-3.png](image_logs/pngs/chapter9-3.png)|
|`chapter10-1`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter10-1) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter9-3...chapter10-1)|[chapter10-1.png](image_logs/pngs/chapter10-1.png)|
|`chapter10-2`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter10-2) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter10-1...chapter10-2)|[chapter10-2.png](image_logs/pngs/chapter10-2.png)|
|`chapter10-close-look`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter10-close-look) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter10-2...chapter10-close-look)|[chapter10-close-look.png](image_logs/pngs/chapter10-close-look.png)|
|`chapter11`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter11) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter10-close-look...chapter11)|[chapter11.png](image_logs/pngs/chapter11.png)|
|`chapter12`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/chapter12) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/chapter11...chapter12)|[chapter12.png](image_logs/pngs/chapter12.png)|

## References

Here is useful references.

* [iyahoo/clj-ray-tracing](https://github.com/iyahoo/clj-ray-tracing) (Clojure)
* [petershirley/raytracinginoneweekend](https://github.com/petershirley/raytracinginoneweekend) (C++)
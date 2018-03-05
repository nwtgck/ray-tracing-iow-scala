# ray-tracing-iow
[![Build Status](https://travis-ci.com/nwtgck/ray-tracing-iow-scala.svg?token=TuxNpqznwwyy7hyJwBVm&branch=master)](https://travis-ci.com/nwtgck/ray-tracing-iow-scala)

[Ray Tracing in One Weekend](http://in1weekend.blogspot.jp/2016/01/ray-tracing-in-one-weekend.html) written in Scala

## Run

```bash
./make_jar.sh
./run_jar.sh > out.ppm
```

(Why not `sbt run`? Because it contains `[info] ...` logs. They aren't useless to use `>` redirect.)

## References

Here is useful references.

* [iyahoo/clj-ray-tracing](https://github.com/iyahoo/clj-ray-tracing) (Clojure)
* [petershirley/raytracinginoneweekend](https://github.com/petershirley/raytracinginoneweekend) (C++)
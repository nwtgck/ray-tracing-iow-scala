# ray-tracing-iow

[Ray Tracing in One Weekend](http://in1weekend.blogspot.jp/2016/01/ray-tracing-in-one-weekend.html) written in Scala

## Run

```bash
sbt assembly
java -jar ./target/scala-2.12/ray-tracing-iow.jar
```

(Why not `sbt run`? Because it contains `[info] ...` logs. They aren't useless to use `>` redirect.)

## References

Here is useful references.

* [iyahoo/clj-ray-tracing](https://github.com/iyahoo/clj-ray-tracing) (Clojure)
* [petershirley/raytracinginoneweekend](https://github.com/petershirley/raytracinginoneweekend) (C++)
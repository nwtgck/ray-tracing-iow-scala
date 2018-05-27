name := "ray-tracing-iow"

version := "0.1.0"

scalaVersion := "2.12.4"

assemblyJarName in assembly := "ray-tracing-iow.jar"

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.6.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
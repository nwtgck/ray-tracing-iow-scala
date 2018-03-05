package io.github.nwtgck.ray_tracing_iow

case class Color3(r: Float, g: Float, b: Float){

  // Addition
  @inline def +(that: Color3): Color3 = Color3(r+that.r, g+that.g, b+that.b)
  // Scaling (*)
  @inline def *(t: Float): Color3 = Color3(r*t, g*t, b*t)


  @inline private def colorElemToInt(colorElem: Float): Int = (255.99 * colorElem).toInt
  val ir: Int = colorElemToInt(r)
  val ig: Int = colorElemToInt(g)
  val ib: Int = colorElemToInt(b)
}

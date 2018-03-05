package io.github.nwtgck.ray_tracing_iow

case class Color3(r: Float, g: Float, b: Float){
  private def colorElemToInt(colorElem: Float): Int = (255.99 * colorElem).toInt

  val ir: Int = colorElemToInt(r)
  val ig: Int = colorElemToInt(g)
  val ib: Int = colorElemToInt(b)
}

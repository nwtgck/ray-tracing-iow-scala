package io.github.nwtgck.ray_tracing_iow

import java.io.PrintStream

object Main {
  def main(args: Array[String]): Unit = {

    // NOTE: You can change another output stream easily
    val out: PrintStream = System.out

    val nx: Int = 200
    val ny: Int = 100

    out.println(
      s"""P3
         |${nx} ${ny}
         |255""".stripMargin)

    for{
      j <- ny - 1 to 0 by -1
      i <- 0 until nx
    } {
      val r: Float = i.toFloat / nx
      val g: Float = j.toFloat / ny
      val b: Float = 0.2f
      def colorElemToInt(colorElem: Float): Int = (255.99 * colorElem).toInt
      val ir: Int = colorElemToInt(r)
      val ig: Int = colorElemToInt(g)
      val ib: Int = colorElemToInt(b)

      out.println(s"${ir} ${ig} ${ib}")
    }

    out.close()
  }
}

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

      val col: Color3 = Color3(
        r = i.toFloat / nx,
        g = j.toFloat / ny,
        b = 0.2f
      )
      out.println(s"${col.ir} ${col.ig} ${col.ib}")
    }

    out.close()
  }
}

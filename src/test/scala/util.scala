package be.adamv.momentum

import be.adamv.momentum.util.*
import munit.FunSuite

import scala.collection.mutable.ListBuffer


class UtilTest extends FunSuite:
  test("source deplete") {
    val xs = 1 to 9
    val ys = ListBuffer[Int]()
    deplete(xs)(ys.addOne)
    assert(xs == ys)
  }

  test("sink trace") {
    val xs = 1 to 9
    val ys = ListBuffer[Int]()
    xs.foreach(trace(ys))
    assert(xs == ys)
  }

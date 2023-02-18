package be.adamv.momentum.test

import be.adamv.momentum.tick
import be.adamv.momentum.util.*
import munit.FunSuite

import scala.collection.mutable.ListBuffer
//
//
class UtilTest extends FunSuite:
  test("sequential") {
    val a = 0 to 4
    val b = 5 to 9
    val src_a = deplete(a)
    val src_b = deplete(b)
    val (log, res) = newTrace[Int]()
    src_a.adapt(log).tick()
    src_b.adapt(log).tick()
    assert(res() == (a ++ b))
  }
//  test("source deplete") {
//    val xs = 1 to 9
//    val ys = ListBuffer[Int]()
//    deplete(xs)(ys.addOne)
//    assert(xs == ys)
//  }
//
//  test("sink trace") {
//    val xs = 1 to 9
//    val ys = ListBuffer[Int]()
//    xs.foreach(trace(ys))
//    assert(xs == ys)
//  }

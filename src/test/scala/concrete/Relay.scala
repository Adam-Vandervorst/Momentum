package be.adamv.momentum.test
package concrete

import be.adamv.momentum.{*, given}
import be.adamv.momentum.Tags.*
import be.adamv.momentum.util.*
import be.adamv.momentum.concrete.*
import munit.FunSuite


import scala.collection.mutable.ListBuffer


class RelayTest extends FunSuite:
  test("source sink relay") {
    val a = 0 to 4
    val b = 5 to 9
    val src_a = deplete(a)
    val src_b = deplete(b)
    val relay_ab = Relay[Int]
    val (sink, log) = newTrace[Int]()
    relay_ab.adapt(sink).tick()
    src_a.adapt(relay_ab).tick()
    src_b.adapt(relay_ab).tick()
    assert(log() == (a ++ b))
  }

  test("source sink relay map contramap") {
    val a = 0 to 4
    val b = 5 to 9
    val src_a = deplete(a)
    val src_b = deplete(b)
    val relay_ab = Relay[Int]
    val (doubles, v) = newTrace[Int]()
    val (halves, w) = newTrace[Double]()
    relay_ab.map(_ * 2).adapt(doubles).tick()
    relay_ab.adapt(halves.contramap(_ / 2)).tick()
    src_a.adapt(relay_ab).tick()
    src_b.adapt(relay_ab).tick()
    assert(v().toSet == (a ++ b).map(_ * 2).toSet)
    assert(w().toSet == (a ++ b).map(_ / 2).toSet)
  }

  test("diamond") {
    val r = new Relay[Int]
    val (pairs, res) = newTrace[(Int, Boolean)]()
    val isPositive = r.map(_ > 0)
    val doubledNumbers = r.map(_ * 2)
    val combinedStream = doubledNumbers.zipLeft(isPositive)
    combinedStream.adapt(pairs).tick()
    r.set(-1)
    r.set(1)
    assert(res() == List((-2, false), (2, true)))
  }

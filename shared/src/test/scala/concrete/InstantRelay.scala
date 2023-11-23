package be.adamv.momentum.test
package concrete

import be.adamv.momentum.{*, given}
import be.adamv.momentum.Tags.*
import be.adamv.momentum.util.*
import be.adamv.momentum.concrete.*
import munit.FunSuite


class InstantRelayTest extends FunSuite:
  test("source sink relay") {
    val a = 0 to 4
    val b = 5 to 9
    val src_a = deplete(a)
    val src_b = deplete(b)
    val relay_ab = InstantRelay[Int]
    val log = Trace[Int]()
    relay_ab.adaptNow(log)
    src_a.adaptNow(relay_ab)
    src_b.adaptNow(relay_ab)
    assert(log.value.reverse == (a ++ b))
  }

  test("source sink relay map contramap") {
    val a = 0 to 4
    val b = 5 to 9
    val src_a = deplete(a)
    val src_b = deplete(b)
    val relay_ab = InstantRelay[Int]
    val doubles = Trace[Int]()
    val halves = Trace[Double]()
    relay_ab.map(_ * 2).adaptNow(doubles)
    relay_ab.adaptNow(halves.contramap(_ / 2))
    src_a.adaptNow(relay_ab)
    src_b.adaptNow(relay_ab)
    assert(doubles.value.toSet == (a ++ b).map(_ * 2).toSet)
    assert(halves.value.toSet == (a ++ b).map(_ / 2).toSet)
  }

  test("diamond") {
    val r = new InstantRelay[Int]
    val pairs = Trace[(Int, Boolean)]()
    val isPositive = r.map(_ > 0)
    val doubledNumbers = r.map(_ * 2)
    val combinedStream = doubledNumbers.zipLeft(isPositive)
    combinedStream.adaptNow(pairs)
    r.set(-1)
    r.set(1)
    println(pairs.value.reverse)
    assert(pairs.value.reverse == List((-2, false), (2, true)))
  }

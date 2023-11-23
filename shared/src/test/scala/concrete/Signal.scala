package be.adamv.momentum.test
package concrete

import be.adamv.momentum.{*, given}
import be.adamv.momentum.Tags.*
import be.adamv.momentum.util.*
import be.adamv.momentum.concrete.*
import munit.FunSuite


class SignalTest extends FunSuite:
  test("diamond") {
    val r = Signal[Int](0)
    val pairs = Trace[(Int, Boolean)]()
    val isPositive = r.map(_ > 0)
    val doubledNumbers = r.map(_ * 2)
    val combinedStream = doubledNumbers.merge(isPositive)
    val subscription = combinedStream.adapt(pairs)
    subscription.set(true)
    r.set(-1)
    r.set(1)
    println(pairs.value)
//    assert(pairs.value.reverse == List((-2, false), (2, true)))
  }

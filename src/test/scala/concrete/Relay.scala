//package be.adamv.momentum
//package concrete
//
//import be.adamv.momentum.util.*
//import be.adamv.momentum.concrete.*
//import munit.FunSuite
//
//import scala.collection.mutable.ListBuffer
//
//
//class RelayTest extends FunSuite:
//  test("source sink relay") {
//    val a = 0 to 4
//    val b = 5 to 9
//    val src_a = deplete(a)
//    val src_b = deplete(b)
//    val relay_ab = Relay[Int]
//    val log = ListBuffer[Int]()
//    val sink = trace(log)
//    relay_ab.adaptor(sink)
//    src_a(relay_ab.setter)
//    src_b(relay_ab.setter)
//    assert(log.toSet == (a ++ b).toSet)
//  }
//
//  test("source sink relay map contramap") {
//    val a = 0 to 4
//    val b = 5 to 9
//    val src_a = deplete(a)
//    val src_b = deplete(b)
//    val relay_ab = Relay[Int]
//    val v = ListBuffer[Int]()
//    val w = ListBuffer[Double]()
//    val doubles = trace(v)
//    val halves = trace(w)
//    relay_ab.adaptor.mapS[Int](_.contramap(_ * 2))(doubles)
//    relay_ab.adaptor(halves.contramap(_ / 2))
//    src_a(relay_ab.setter)
//    src_b(relay_ab.setter)
//    assert(v.toSet == (a ++ b).map(_ * 2).toSet)
//    assert(w.toSet == (a ++ b).map(_ / 2).toSet)
//  }
//
//  test("diamond") {
//    val (numbers, feed) = callback[Int]
//    val (pairs, res) = newTrace[(Int, Boolean)]()
//    val isPositive: Producer[Boolean, Unit] & RBuffered[Boolean] = numbers.mapS[Boolean](_.contramap(_ > 0)).buffered
//    val doubledNumbers: Producer[Int, Unit] & RBuffered[Int] = numbers.mapS[Int](_.contramap(_ * 2)).buffered
//    val combinedStream: Producer[(Int, Boolean), Unit] = doubledNumbers.zipLeft(isPositive)
//    combinedStream(pairs)
//    feed(-1)
//    feed(1)
//    assert(res() == List((-2, false), (2, true)))
//  }
//
//class LazyRelay extends FunSuite:
//  test("diamond") {
//
//  }
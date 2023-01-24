package be.adamv.momentum.test
package concrete

import be.adamv.momentum.{*, given}
import be.adamv.momentum.Tags.*
import be.adamv.momentum.util.*
import be.adamv.momentum.concrete.*
import munit.FunSuite

import scala.collection.mutable.ListBuffer


class NodeTest extends FunSuite:
//  test("basic map merge") {
//    val numbers = Node.start[Int, Unit]
//    val double = numbers.map(_*2)
//    val isPositive = numbers.map(_ > 0)
//    val combined = double merge isPositive
//
//    val (trace, res) = newTrace[Any]()
//    val feed = combined.adapt(trace)
//
//    feed(-1)
//    feed(1)
//    assert(res() == List((-2, false), (2, true)))
//  }
//
//  test("basic chain map merge") {
//    val numbers = Node.start[Int, Unit]
//    val l0 = numbers.map(_ * 2).map(_ * 3).map(_ * 5)
//    val r0 = numbers.map(_ * 2*3*5)
//    val combined0 = l0 merge r0
//    val l1 = l0.map(_ + 2 + 4).map(_ + 8 + 16)
//    val r1 = r0.map(_ + 2).map(_ + 4).map(_ + 8).map(_ + 16)
//    val combined1 = l1 merge r1
//    val l2 = combined1.map{ case (a, b) => (a + b) % 17 }
//    val r2 = combined1.map{ case (a, b) => (a % 17, b % 17)}.map{ case (a, b) => (a + b) % 17 }
//    val combined2 = l2 merge r2
//
//    val (trace, res) = newTrace[Any]()
//
//    deplete(1 to 10).adapt(combined0.adapt(trace)).tick()
//    assert(res() == List((30,30), (60,60), (90,90), (120,120), (150,150), (180,180), (210,210), (240,240), (270,270), (300,300)))
//    deplete(1 to 10).adapt(combined1.adapt(trace)).tick()
//    assert(res() == List((60,60), (90,90), (120,120), (150,150), (180,180), (210,210), (240,240), (270,270), (300,300), (330,330)))
//    deplete(1 to 10).adapt(combined2.adapt(trace)).tick()
//    assert(res() == List((1,1), (10,10), (2,2), (11,11), (3,3), (12,12), (4,4), (13,13), (5,5), (14,14)))
//  }


  test("tagged") {
    val numbersa = Node.named[Int, Unit]("a")
    val numbersb = Node.named[Int, Unit]("b")
    val numbersc = Node.named[Int, Unit]("c")

    val numbersma = numbersa.map(x => x + 10 )
    val l = (numbersma smartMerge numbersb) smartMerge numbersc
    val la = l.map{ case ((a, b), c) => s"a: $a  b: $b  c: $c" }

    val (trace, res) = newTrace[Any]()
    val ls = la.adapt(trace)

    ls(1, 2, 3)
    assert(res() == List("a: 11  b: 2  c: 3"))
  }

//  test("tagged") {
//    val ra = require[Int, "a"]
//    val rb = require[Double, "b"]
//    val rc = require[Int, "c"]
//
//
//    println(feedAll(ra.fill(3), rb.fill(5.5), rc.fill(1)))
//  }

//  test("basic continuing") {
//    val s = Seq(1, 2, 3)
//    val depl = Node.continuing(deplete(s))
//    val r = depl.map(_*3)
//
//    val (trace, res) = newTrace[Any]()
//    val t = r.adapt(trace)
//
//    t.tick()
//    assert(res() == s.map(_*3))
//  }

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

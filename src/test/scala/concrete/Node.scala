package be.adamv.momentum.test
package concrete

import be.adamv.momentum.{*, given}
import be.adamv.momentum.Tags.*
import be.adamv.momentum.util.*
import be.adamv.momentum.concrete.*
import munit.FunSuite


class NodeTest extends FunSuite:
  test("map smartMerge fork") {
    val numbers = Node.named[Int, Unit]("a")
    val double = numbers.map(_*2)
    val isPositive = numbers.map(_ > 0)
    val combined = double smartMerge isPositive

    val (trace, res) = newTrace[Any]()
    val feed: Sink[Int, Unit] = combined.adapt(trace).asSingle

    feed.set(-1)
    feed.set(1)
    assert(res() == List((-2, false), (2, true)))
  }

  test("chain map smartMerge") {
    val numbers = Node.named[Int, Unit]("numbers")
    val l0 = numbers.map(_ * 2).map(_ * 3).map(_ * 5)
    val r0 = numbers.map(_ * 2*3*5)
    val combined0 = l0 smartMerge r0
    val l1 = l0.map(_ + 2 + 4).map(_ + 8 + 16)
    val r1 = r0.map(_ + 2).map(_ + 4).map(_ + 8).map(_ + 16)
    val combined1 = l1 smartMerge r1
    val l2 = combined1.map{ case (a, b) => (a + b) % 17 }
    val r2 = combined1.map{ case (a, b) => (a % 17, b % 17)}.map{ case (a, b) => (a + b) % 17 }
    val combined2 = l2 smartMerge r2

    val (trace, res) = newTrace[Any]()

    deplete(1 to 10).adapt(combined0.adapt(trace).asSingle).tick()
    assert(res() == List((30,30), (60,60), (90,90), (120,120), (150,150), (180,180), (210,210), (240,240), (270,270), (300,300)))
    deplete(1 to 10).adapt(combined1.adapt(trace).asSingle).tick()
    assert(res() == List((60,60), (90,90), (120,120), (150,150), (180,180), (210,210), (240,240), (270,270), (300,300), (330,330)))
    deplete(1 to 10).adapt(combined2.adapt(trace).asSingle).tick()
    assert(res() == List((1,1), (10,10), (2,2), (11,11), (3,3), (12,12), (4,4), (13,13), (5,5), (14,14)))
  }

  test("map smartMerge assoc") {
    val numbersa = Node.named[Int, Unit]("a")
    val numbersb = Node.named[Int, Unit]("b")
    val numbersc = Node.named[Int, Unit]("c")

    val numbersma = numbersa.map(x => x + 10 )
    val l = (numbersma smartMerge numbersb) smartMerge numbersc
    val la = l.map{ case ((a, b), c) => s"a: $a  b: $b  c: $c" }

    val (trace, res) = newTrace[Any]()
    val ls = la.adapt(trace)

    ls.set("a" -> 1, "b" -> 2, "c" -> 3)
    assert(res() == List("a: 11  b: 2  c: 3"))
  }

  test("continuing") {
    val s = Seq(1, 2, 3)
    val depl = Node.succeeding(deplete(s))
    val r = depl.map(_*3)

    val (trace, res) = newTrace[Any]()
    val t = r.adapt(trace)

    t.tick()
    assert(res() == s.map(_*3))
  }

  test("diamond") {
    val numbers = Node.named[Int, Unit]("x")
    val (pairs, res) = newTrace[(Int, Boolean)]()
    val isPositive = numbers.map(_ > 0)
    val doubledNumbers = numbers.map(_ * 2)
    val combinedStream = doubledNumbers smartMerge isPositive
    val feed = combinedStream.adapt(pairs).asSingle
    feed.set(-1)
    feed.set(1)
    assert(res() == List((-2, false), (2, true)))
  }

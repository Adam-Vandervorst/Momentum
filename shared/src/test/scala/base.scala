package be.adamv.momentum

import be.adamv.momentum.concrete.Trace
import be.adamv.momentum.util.deplete
import be.adamv.momentum.{*, given}
//import be.adamv.momentum.concrete.*
import munit.FunSuite



class Nats extends Source[Int, Unit]:
  var current: Int = 0
  override def get(e: Unit): Int =
    val ret = current
    current += 1
    ret

class BaseTest extends FunSuite:
  test("map contramap") {
    val a = 0 to 4
    val b = 5 to 9
    val src_a = deplete(a)
    val src_b = deplete(b)
    val doubles = Trace[Int]()
    val halves = Trace[Double]()
    src_a.map(_ * 2).adaptNow(doubles)
    src_b.adaptNow(halves.contramap(_.toDouble / 2))
    assert(doubles.value.reverse == a.map(_ * 2))
    assert(halves.value.reverse == b.map(_.toDouble / 2))
  }

  test("squares above 100") {
    val numbers: Source[Int, Unit] = Nats()
    val squares: Source[Int, Unit] = numbers.map(x => x*x)
    val above100: Source[Int, Unit] = squares.filter(x => x > 100)

    assert(above100.nvalues(10) == List(121, 144, 169, 196, 225, 256, 289, 324, 361, 400))
  }


//  test("source sink") {
//    val xs = 1 to 9
//    val (buffer, res) = newTrace[Int]()
//    deplete(xs) --> buffer
//    assert(xs == res())
//  }
//
//  test("source callback") {
//    var a = 0
//    var b = 0
//    var c = 0
//    val (src, f) = callback[Boolean]
//    f(true)
//    f(false)
//    f(true)
//    src(if _ then a = a + 1)
//    src(if _ then b = b + 1)
//    f(true)
//    src(if _ then c = c + 1)
//    f(false)
//    f(true)
//    assert(a == b)
//    assert(a == 2)
//    assert(c == 1)
//  }
//
//  test("source tapEach") {
//    val xs = 1 to 9
//    val ys = ListBuffer[Int]()
//    deplete(xs).mapS(_.eachTapped(ys.addOne))(_ => ())
//    assert(xs == ys)
//  }
//
//  test("source mapTo") {
//    var a = 0
//    var b = 0
//    var unevaluated = true
//    val (src, f) = callback[Boolean]
//    val true_src: Producer[Boolean, Unit] = src.mapS(_.contramapTo({unevaluated = false; true}))
//    src(if _ then a = a + 1)
//    f(true)
//    true_src(if _ then b = b + 1)
//    assert(unevaluated)
//    f(true)
//    f(false)
//    assert(a == 2)
//    assert(b == 2)
//  }
//
//  test("source map") {
//    var a = 0
//    var b = 0
//    val (src, f) = callback[Boolean]
//    val neg_src: Producer[Boolean, Unit] = src.map(!_)
//    src(if _ then a = a + 1)
//    neg_src(if _ then b = b + 1)
//    f(true)
//    f(true)
//    f(false)
//    assert(a == 2)
//    assert(b == 1)
//  }
//
//  test("source deplete map") {
//    val xs = 1 to 9
//    val ys = ListBuffer[Int]()
//    val zs = ListBuffer[Int]()
//    val src = deplete(xs)
//    val double_src: Producer[Int, Unit] = src.map(_*2)
//    src(ys.addOne)
//    double_src(zs.addOne)
//    assert(xs == ys)
//    assert(xs.map(_*2) == zs)
//  }
//
//  test("source foldLeft") {
//    var a = 0
//    var b = 0
//    val (src, f) = callback[Boolean]
//    val pos_count: Producer[Int, Unit] = src.mapS(_.scan(0)((t, b) => t + (if b then 1 else 0)))
//    val neg_count: Producer[Int, Unit] = src.mapS(_.scan(0)((t, b) => t + (if b then 0 else 1)))
//    neg_count(a = _)
//    f(true)
//    assert(a == 0)
//    pos_count(b = _)
//    f(true)
//    assert(a == 0 && b == 1)
//    f(false)
//    f(true)
//    f(false)
//    assert(a == 2 && b == 2)
//  }
//
//  test("sink contramap") {
//    val xs = 1 to 9
//    val ys = ListBuffer[Double]()
//    val f = (x: Int) => Math.sqrt(x.toDouble)
//    val double_trace = trace(ys).contramap(f)
//    xs.foreach(double_trace)
//    assert(xs.map(f) == ys)
//  }
//
//  test("sink scan") {
//    val xs = 1 to 5
//    val ys = ListBuffer[Int]()
//    val squared = trace(ys).scan[Int, Int](10)(_ + _)
//    xs.foreach(squared)
//    assert(xs.scanLeft(10)(_ + _) == ys)
//  }
//
//class NonlinearBaseTest extends FunSuite:
//  test("source filter") {
//    var a = 0
//    var b = 0
//    var c = 0
//    val (src, f) = callback[Boolean]
//    val pos_src = src.mapS(_.contrafilter(identity))
//    val neg_src = src.mapS(_.contrafilter(!_))
//    src(if _ then a = a + 1)
//    pos_src(_ => b = b + 1)
//    neg_src(_ => c = c + 1)
//    f(true)
//    f(true)
//    f(false)
//    assert(a == b)
//    assert(c == 1)
//  }
//
//  test("source deplete filter") {
//    val xs = 1 to 9
//    val ys = ListBuffer[Int]()
//    val zs = ListBuffer[Int]()
//    val src = deplete(xs)
//    val even_src = src.mapS[Int](_.contrafilter(_ % 2 == 0))
//    src(ys.addOne)
//    even_src(zs.addOne)
//    assert(xs == ys)
//    assert(xs.filter(_ % 2 == 0) == zs)
//  }
//
//  test("sink contracollect") {
//    val xs = Seq(Some(1), None, Some(2), None, Some(3))
//    val ys = ListBuffer[Int]()
//    val f: PartialFunction[Option[Int], Int] = {
//      case Some(x) => x * x
//    }
//    val squared = trace(ys).contracollect(f)
//    xs.foreach(squared)
//    assert(xs.collect(f) == ys)
//  }
//
//  test("source sink filter contramap") {
//    val xs = Seq[Option[Int]](Some(1), None, Some(2), None, Some(3))
//    val ys = ListBuffer[Int]()
//    val f: PartialFunction[Option[Int], Int] = {
//      case Some(x) => x * x
//    }
//    val src = deplete(xs)
//    val snk = trace(ys)
//    src.mapS[Option[Int]](_.contrafilter(f.isDefinedAt)) --> snk.contramap(f.apply)
//    assert(xs.collect(f) == ys)
//  }
//
//  test("source deplete collect") {
//    val xs = 1 to 9
//    val ys = ListBuffer[Int]()
//    val zs = ListBuffer[Int]()
//    val perfect = (x: Int) => Some(math.sqrt(x)).filter(_.isWhole).map(_.toInt)
//    val src = deplete(xs)
//    val perfect_src = src.mapS[Int](_.contracollect(perfect.unlift))
//    src(ys.addOne)
//    perfect_src(zs.addOne)
//    assert(xs == ys)
//    assert(xs.collect(perfect.unlift) == zs)
//  }
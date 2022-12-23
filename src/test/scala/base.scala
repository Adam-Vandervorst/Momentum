package be.adamv.momentum

import be.adamv.momentum.util.*
import be.adamv.momentum.concrete.*
import munit.FunSuite

import scala.collection.mutable.ListBuffer


class BaseTest extends FunSuite:
  test("source sink") {
    val xs = 1 to 9
    val (buffer, res) = newTrace[Int]()
    deplete(xs) --> buffer
    assert(xs == res())
  }

  test("source callback") {
    var a = 0
    var b = 0
    var c = 0
    val (src, f) = callback[Boolean]
    f(true)
    f(false)
    f(true)
    src.register(if _ then a = a + 1)
    src.register(if _ then b = b + 1)
    f(true)
    src.register(if _ then c = c + 1)
    f(false)
    f(true)
    assert(a == b)
    assert(a == 2)
    assert(c == 1)
  }

  test("source tapEach") {
    val xs = 1 to 9
    val ys = ListBuffer[Int]()
    deplete(xs).tapEach(ys.addOne).register(_ => ())
    assert(xs == ys)
  }

  test("source mapTo") {
    var a = 0
    var b = 0
    var unevaluated = true
    val (src, f) = callback[Boolean]
    val true_src = src.mapTo({unevaluated = false; true})
    src.register(if _ then a = a + 1)
    f(true)
    true_src.register(if _ then b = b + 1)
    assert(unevaluated)
    f(true)
    f(false)
    assert(a == 2)
    assert(b == 2)
  }

  test("source map") {
    var a = 0
    var b = 0
    val (src, f) = callback[Boolean]
    val neg_src = src.map(!_)
    src.register(if _ then a = a + 1)
    neg_src.register(if _ then b = b + 1)
    f(true)
    f(true)
    f(false)
    assert(a == 2)
    assert(b == 1)
  }

  test("source deplete map") {
    val xs = 1 to 9
    val ys = ListBuffer[Int]()
    val zs = ListBuffer[Int]()
    val src = deplete(xs)
    val double_src = src.map(_*2)
    src.register(ys.addOne)
    double_src.register(zs.addOne)
    assert(xs == ys)
    assert(xs.map(_*2) == zs)
  }

  test("source filter") {
    var a = 0
    var b = 0
    var c = 0
    val (src, f) = callback[Boolean]
    val pos_src = src.filter(identity)
    val neg_src = src.filter(!_)
    src.register(if _ then a = a + 1)
    pos_src.register(_ => b = b + 1)
    neg_src.register(_ => c = c + 1)
    f(true)
    f(true)
    f(false)
    assert(a == b)
    assert(c == 1)
  }

  test("source deplete filter") {
    val xs = 1 to 9
    val ys = ListBuffer[Int]()
    val zs = ListBuffer[Int]()
    val src = deplete(xs)
    val even_src = src.filter(_ % 2 == 0)
    src.register(ys.addOne)
    even_src.register(zs.addOne)
    assert(xs == ys)
    assert(xs.filter(_ % 2 == 0) == zs)
  }

  test("source sink filter contramap") {
    val xs = Seq[Option[Int]](Some(1), None, Some(2), None, Some(3))
    val ys = ListBuffer[Int]()
    val f: PartialFunction[Option[Int], Int] = {case Some(x) => x*x}
    val src = deplete(xs)
    val snk = trace(ys)
    snk.contramap(f.apply) <-- src.filter(f.isDefinedAt)
    assert(xs.collect(f) == ys)
  }

  test("source deplete collect") {
    val xs = 1 to 9
    val ys = ListBuffer[Int]()
    val zs = ListBuffer[Int]()
    val perfect = (x: Int) => Some(math.sqrt(x)).filter(_.isWhole).map(_.toInt)
    val src = deplete(xs)
    val perfect_src = src.collect(perfect.unlift)
    src.register(ys.addOne)
    perfect_src.register(zs.addOne)
    assert(xs == ys)
    assert(xs.collect(perfect.unlift) == zs)
  }

  test("source foldLeft") {
    var a = 0
    var b = 0
    val (src, f) = callback[Boolean]
    val pos_count = src.scan(0)((t, b) => t + (if b then 1 else 0))
    val neg_count = src.scan(0)((t, b) => t + (if b then 0 else 1))
    neg_count.register(a = _)
    f(true)
    assert(a == 0)
    pos_count.register(b = _)
    f(true)
    assert(a == 0 && b == 1)
    f(false)
    f(true)
    f(false)
    assert(a == 2 && b == 2)
  }

  test("source dedup") {
    var a = 0
    var b = 0
    val s = "TTTFTFFT"
    val src = deplete(s.map(_ == 'T'))
    val changes = src.buffered.dedup
    val occ_count = src.scan(0)((t, _) => t + 1)
    val change_count = changes.scan(0)((t, _) => t + 1)
    occ_count.register(a = _)
    change_count.register(b = _)
    assert(a == s.length)
    assert(b == 1 + s.sliding(2).count(p => p == "TF" || p == "FT"))
  }

  test("sink contramap") {
    val xs = 1 to 9
    val ys = ListBuffer[Double]()
    val f = (x: Int) => Math.sqrt(x.toDouble)
    val double_trace = trace(ys).contramap(f)
    xs.foreach(double_trace.set)
    assert(xs.map(f) == ys)
  }

  test("sink contracollect") {
    val xs = Seq(Some(1), None, Some(2), None, Some(3))
    val ys = ListBuffer[Int]()
    val f: PartialFunction[Option[Int], Int] = {case Some(x) => x*x}
    val squared = trace(ys).contracollect(f)
    xs.foreach(squared.set)
    assert(xs.collect(f) == ys)
  }

  test("sink scan") {
    val xs = 1 to 5
    val ys = ListBuffer[Int]()
    val squared = trace(ys).scan[Int, Int](10)(_ + _)
    xs.foreach(squared.set)
    assert(xs.scanLeft(10)(_ + _) == ys)
  }

  test("sink onNew") {
    var a = 0
    var b = 0
    val s = "TTTFTFFT"
    val a_sink: Sink[Boolean] = _ => a += 1
    val b_sink: Sink[Boolean] = _ => b += 1
    val change_update = b_sink.buffered.onNew
    val occ_count = a_sink.contramap(_ == 'T')
    val change_count = change_update.contramap(_ == 'T')
    s.foreach(occ_count.set)
    s.foreach(change_count.set)
    assert(a == s.length)
    assert(b == 1 + s.sliding(2).count(p => p == "TF" || p == "FT"))
  }

  test("source sink relay") {
    val a = 0 to 4
    val b = 5 to 9
    val src_a = deplete(a)
    val src_b = deplete(b)
    val relay_ab = Relay[Int]
    val log = ListBuffer[Int]()
    val sink = trace(log)
    relay_ab --> sink
    relay_ab <-- src_a <-- src_b
    assert(log.toSet == (a ++ b).toSet)
  }

  test("source sink relay map contramap") {
    val a = 0 to 4
    val b = 5 to 9
    val src_a = deplete(a)
    val src_b = deplete(b)
    val relay_ab = Relay[Int]
    val v = ListBuffer[Int]()
    val w = ListBuffer[Double]()
    val doubles = trace(v)
    val halves = trace(w)
    relay_ab.map(_*2) --> doubles
    relay_ab --> halves.contramap(_/2)
    relay_ab <-- src_a <-- src_b
    assert(v.toSet == (a ++ b).map(_*2).toSet)
    assert(w.toSet == (a ++ b).map(_/2).toSet)
  }

  test("relay combine") {
    var ab: Option[(Boolean, Boolean)] = None
    val (src_a, f_a) = callback[Boolean]
    val (src_b, f_b) = callback[Boolean]
    val relay_a = Relay[Boolean]
    val relay_b = Relay[Boolean]
    src_a --> relay_a
    src_b --> relay_b
    val relay_ab = relay_a combine relay_b
    relay_ab.register(p => ab = Some(p))
    assert(ab.isEmpty)
    f_a(false)
    assert(ab.isEmpty)
    f_a(true)
    assert(ab.isEmpty)
    f_b(true)
    assert(ab.contains((true, true)))
    f_b(false)
    assert(ab.contains((true, false)))
  }

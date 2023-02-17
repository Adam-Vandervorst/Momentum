package be.adamv.momentum

import munit.FunSuite


class StorageTest extends FunSuite:
  ()
//  test("source dedup") {
//    var a = 0
//    var b = 0
//    val s = "TTTFTFFT"
//    val src = deplete(s.map(_ == 'T'))
//    val changes = src.buffered.dedup
//    val occ_count = src.scan(0)((t, _) => t + 1)
//    val change_count = changes.scan(0)((t, _) => t + 1)
//    occ_count.register(a = _)
//    change_count.register(b = _)
//    assert(a == s.length)
//    assert(b == 1 + s.sliding(2).count(p => p == "TF" || p == "FT"))
//  }

//test("sink onNew") {
//  var a = 0
//  var b = 0
//  val s = "TTTFTFFT"
//  val a_sink: Setter[Boolean, Unit] = _ => a += 1
//  val b_sink: Setter[Boolean, Unit] = _ => b += 1
//  val change_update = b_sink.buffered.onNew
//  val occ_count = a_sink.contramap(_ == 'T')
//  val change_count = change_update.contramap(_ == 'T')
//  s.foreach(occ_count.set)
//  s.foreach(change_count.set)
//  assert(a == s.length)
//  assert(b == 1 + s.sliding(2).count(p => p == "TF" || p == "FT"))
//}

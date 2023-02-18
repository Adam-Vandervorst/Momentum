package be.adamv.momentum

import be.adamv.momentum.concrete.Relay

import collection.mutable.ListBuffer


type AssumeTuple[X] = X & Tuple


package util:
  def deplete[T](xs: Seq[T]): Descend[Unit, T, Unit] = (snk: Sink[T, Unit]) => _ =>
    xs.foreach(snk.set)

  def newTrace[X](): (Sink[X, Unit], () => List[X]) =
    val xs = ListBuffer.empty[X]
    ((x: X) => xs.addOne(x), () => {
      val res = xs.result(); xs.clear(); res
    })

//  def newTicker(): (Producer[Unit, Unit], Int => Unit) =
//    val (src, tick) = callback[Unit]
//    (src, i => (1 to i).foreach(_ => tick(())))

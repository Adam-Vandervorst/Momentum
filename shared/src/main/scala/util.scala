package be.adamv.momentum

import be.adamv.momentum.concrete.InstantRelay

import collection.mutable.ListBuffer


type AssumeTuple[X] = X & Tuple


package util:
  def deplete[T](xs: Seq[T]): Descend[Unit, T, Unit] = (snk: Sink[T, Unit]) => _ =>
    xs.foreach(snk.set)

  def callback[X](): (Sink[X, Unit], Descend[Unit, X, Unit]) =
    val r = new InstantRelay[X]
    (r, r)

//  def newTicker(): (Producer[Unit, Unit], Int => Unit) =
//    val (src, tick) = callback[Unit]
//    (src, i => (1 to i).foreach(_ => tick(())))

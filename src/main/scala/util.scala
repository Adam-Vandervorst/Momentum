package be.adamv.momentum

import be.adamv.momentum.concrete.Relay

import collection.mutable.ListBuffer


package util:
  def deplete[T](xs: Seq[T]): SetAdaptor[T, Unit] = new SetAdaptor[T, Unit]:
    override def apply(sset: Setter[T, Unit]): Unit =
      xs.foreach(sset)

  def trace[T](xs: ListBuffer[T]): Setter[T, Unit] =
    (x: T) => xs.addOne(x)

  def callback[T]: (SetAdaptor[T, Unit], Setter[T, Unit]) =
    val r = new Relay[T]
    (r.adaptor, r.setter)

  def newTrace[X](): (Setter[X, Unit], () => List[X]) =
    val xs = ListBuffer.empty[X]
    ((x: X) => xs.addOne(x), () => {
      val res = xs.result(); xs.clear(); res
    })

  def newTicker(): (SetAdaptor[Unit, Unit], Int => Unit) =
    val (src, tick) = callback[Unit]
    (src, i => (1 to i).foreach(_ => tick(())))

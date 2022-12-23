package be.adamv.momentum

import be.adamv.momentum.concrete.Relay

import collection.mutable.ListBuffer


package util:
  def deplete[T](xs: Seq[T]): Source[T] = new Source[T]:
    override def adapt(s: Sink[T]): Source[_] =
      xs.foreach(s.set)
      this

  def trace[T](xs: ListBuffer[T]): Sink[T] =
    (x: T) => xs.addOne(x)

  def callback[T]: (Source[T], T => Unit) =
    val r = new Relay[T]
    (r, r.set)

  def newTrace[X](): (Sink[X], () => List[X]) =
    val xs = ListBuffer.empty[X]
    ((x: X) => xs.addOne(x), () => {
      val res = xs.result(); xs.clear(); res
    })

  def newTicker(): (Source[Unit], Int => Unit) =
    val (src, tick) = callback[Unit]
    (src, i => (1 to i).foreach(_ => tick(())))

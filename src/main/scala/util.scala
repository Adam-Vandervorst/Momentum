package be.adamv.momentum

//import be.adamv.momentum.concrete.Relay

import collection.mutable.ListBuffer


type AssumeTuple[X] = X & Tuple


package util:
  def deplete[T](xs: Seq[T]): Descend[Unit, T, Unit] = (sset: Sink[T, Unit]) => _ =>
    xs.foreach(sset.apply)

  def newTrace[X](): (Sink[X, Unit], () => List[X]) =
    val xs = ListBuffer.empty[X]
    ((x: X) => xs.addOne(x), () => {
      val res = xs.result(); xs.clear(); res
    })

/*

  def deplete[T](xs: Seq[T]): Producer[T, Unit] = new Producer[T, Unit]:
    override def apply(sset: Sink[T, Unit]): Unit =
      xs.foreach(sset)

  def trace[T](xs: ListBuffer[T]): Sink[T, Unit] =
    (x: T) => xs.addOne(x)

  def callback[T]: (Producer[T, Unit] & RBuffered[T], Sink[T, Unit]) =
    val r = new Relay[T]
    (r.adaptor, r.setter)


  def newTicker(): (Producer[Unit, Unit], Int => Unit) =
    val (src, tick) = callback[Unit]
    (src, i => (1 to i).foreach(_ => tick(())))
*/

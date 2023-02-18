package be.adamv.momentum
package concrete


class Fun[A, B](f: A => B) extends Process[A, B]:
  def adapt(s: Source[A, Unit]): Source[B, Unit] = e => f(s.get(e))
  def adapt(s: Sink[B, Unit]): Sink[A, Unit] = a => s.set(f(a))


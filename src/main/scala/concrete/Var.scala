package be.adamv.momentum
package concrete


class Var[A](initial: A) extends ConcreteValued[A](initial) with Sink[A] with Source[A]:
  private val subs = collection.mutable.Stack[Sink[A]]()

  def adapt(s: Sink[A]): this.type =
    subs.addOne(s)
    this
  def set(a: A): Unit =
    subs.foreach(_.set(a))
    _value = a

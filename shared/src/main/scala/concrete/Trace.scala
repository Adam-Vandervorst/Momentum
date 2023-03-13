package be.adamv.momentum
package concrete


class Trace[A] extends Sink[A, Unit], Source[List[A], Unit]:
  protected var trace: List[A] = Nil

  def clear(): List[A] =
    val t = trace
    trace = Nil
    t

  def unsafeLast: A = trace match
    case head::_ => head
    case Nil => throw RuntimeException("Tried to get an uninitialized value")

  override def get(e: Unit): List[A] =
    trace

  override def set(a: A): Unit =
    trace ::= a

package be.adamv.momentum
package concrete

import scala.compiletime.uninitialized


class Buffer[A] extends Sink[A, Unit], Source[Option[A], Unit]:
  protected var value: A = uninitialized
  var initialized: Boolean = false

  def clear(): Boolean =
    val r = initialized
    initialized = false
    r

  def unsafeValue: A =
    if initialized then value
    else throw RuntimeException("Tried to get an uninitialized value")

  override def get(e: Unit): Option[A] =
    Option.when(initialized)(value)

  override def set(a: A): Unit =
    value = a
    initialized = true

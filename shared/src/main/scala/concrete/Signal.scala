package be.adamv.momentum
package concrete

import scala.reflect.Typeable


class Signal[A](initial: A, val priority: Int = 0) extends Var[A](initial), Descend[Boolean, A, Unit]:
  self =>
  private val subs = scala.collection.mutable.HashSet.empty[Sink[A, Unit]]

  override def adapt(s: Sink[A, Unit]): Sink[Boolean, Unit] =
    b =>
      if b then subs.add(s)
      else subs.remove(s)

  override def set(a: A): Unit =
    value = a
    println(s"Signal $this set to $a with ${subs.size} subs")
    subs.toSeq.sortBy {
      case s: Signal[_] => s.priority
      case _ => -1
    }.foreach { s =>
      println(s"notifying $s with $a")
      s.set(a)
    }

  override def map[B](f: A => B): Signal[B] = MappedSignal(self, f)

  override def tapEach(g: A => Unit): Signal[A] & Source[A, Unit] = TappedSignal(self, g)

  def mergeWith[B, C](merge: (A, B) => C)(other: Signal[B]): Descend[Boolean, C, Unit] & Source[C, Unit] = MergeSignal(self, other, merge)

  def merge[B](other: Signal[B]): Descend[Boolean, (A, B), Unit] & Source[(A, B), Unit] = MergeSignal(self, other, (_, _))


case class MappedSignal[A, B](parent: Signal[A], mapping: A => B) extends Signal[B](mapping(parent.value), parent.priority + 1):
  parent.adapt(a => set(mapping(a))).set(true)

case class TappedSignal[A](parent: Signal[A], tap: A => Unit) extends Signal[A](parent.value, parent.priority + 1)

case class MergeSignal[A, B, C](left: Signal[A], right: Signal[B], merger: (A, B) => C) extends Signal[C](merger(left.value, right.value), (left.priority max right.priority) + 1):
  left.adapt(a => set(merger(a, right.value))).set(true)
  right.adapt(b => set(merger(left.value, b))).set(true)

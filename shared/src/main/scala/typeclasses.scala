package be.adamv.momentum

import scala.compiletime.constValue


trait Spawn[E]:
  def spawn(): E

trait Cease[E]:
  def cease(e: E): Unit

trait Merge[E]:
  def merge(e1: E, e2: E): E

trait Duplicate[E]:
  def duplicate(e: E): (E, E)


inline given Spawn[Unit] with
  inline def spawn(): Unit = ()
inline given Cease[Unit] with
  inline def cease(e: Unit): Unit = ()
inline given Merge[Unit] with
  inline def merge(e1: Unit, e2: Unit): Unit = ()
inline given Duplicate[Unit] with
  inline def duplicate(e: Unit): (Unit, Unit) = ((), ())


trait Inclusion[L, R]:
  def forth: L => R
  def back: PartialFunction[R, L]

given Inclusion[Unit, Boolean] with
  def forth: Unit => Boolean = {case () => true}
  def back: PartialFunction[Boolean, Unit] = {case true => ()}

given Inclusion[Boolean, Int] with
  def forth: Boolean => Int = {case false => 0; case true => 1}
  def back: PartialFunction[Int, Boolean] = {case 0 => false; case 1 => true}

given [A]: Inclusion[A, (A, A)] with
  def forth: A => (A, A) = a => (a, a)
  def back: PartialFunction[(A, A), A] = {case (a1, a2) if a1 == a2 => a1}


extension [A, E](s: Sink[A, E])
  def expanded[B](using i: Inclusion[B, A]): Sink[B, E] = s.contramap(i.forth)
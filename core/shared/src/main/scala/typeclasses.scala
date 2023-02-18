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

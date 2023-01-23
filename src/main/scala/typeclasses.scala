package be.adamv.momentum


trait Default[E]:
  def value: E

inline given Default[Unit] with
  inline def value: Unit = ()

trait Merge[E]:
  def merge(e1: E, e2: E): E

inline given Merge[Unit] with
  inline def merge(e1: Unit, e2: Unit): Unit = ()

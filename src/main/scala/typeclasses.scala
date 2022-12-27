package be.adamv.momentum


trait Default[E]:
  def value: E

inline given Default[Unit] with
  inline def value: Unit = ()


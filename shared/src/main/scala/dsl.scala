package be.adamv.momentum
package dsl

extension [A, E](s: Sink[A, E])
  def <-- [R](d: Descend[R, A, E])(using sr: Spawn[R]): E =
    d.adapt(s).set(sr.spawn())
  def -| (d: Source[A, E])(using se: Spawn[E]): E =
    s.set(d.get(se.spawn()))
  def <-| [R](d: Descend[R, A, E] & Source[A, E])(using m: Merge[E], sr: Spawn[R], se: Spawn[E]): E =
    m.merge(s -| d, s <-- d)

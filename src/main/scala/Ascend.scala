package be.adamv.momentum

@FunctionalInterface
trait Ascend[+R, -A, E]:
  def adapt(s: Source[A, E]): Source[R, E]

  def contramap[B](f: B => A): Ascend[R, B, E] =
    (s: Source[B, E]) => adapt(e => f(s.get(e)))


trait AscendFactory[D[r, a, e] <: Ascend[r, a, e]]:
  def end[A, E]: D[A, A, E]
  def preceding[R, A, E](ascend: Ascend[R, A, E]): Ascend[R, A, E]

object Ascend extends AscendFactory[Ascend]:
  inline def end[A, E]: Ascend[A, A, E] = identity
  inline def preceding[R, A, E](ascend: Ascend[R, A, E]): Ascend[R, A, E] = ascend

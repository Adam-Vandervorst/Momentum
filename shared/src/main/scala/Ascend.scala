package be.adamv.momentum

@FunctionalInterface
trait Ascend[+R, -A, E]:
  def adapt(s: Source[A, E]): Source[R, E]

  def contramap[B](f: B => A): Ascend[R, B, E] =
    (src: Source[B, E]) => adapt(e => f(src.get(e)))

  def eachTapped[AA <: A](f: AA => Unit): Ascend[R, AA, E] =
    (src: Source[AA, E]) => adapt(e => { val a = src.get(e); f(a); a })

trait AscendFactory[D[r, a, e] <: Ascend[r, a, e]]:
  def end[A, E]: D[A, A, E]
  def preceding[R, A, E](ascend: Ascend[R, A, E]): Ascend[R, A, E]

object Ascend extends AscendFactory[Ascend]:
  extension [R, A, E](asc: Ascend[R, A, E])(using d: Duplicate[E])
    def contrafilter(p: A => Boolean): Ascend[R, A, E] =
      src => asc.adapt(src.filter(p))

    def contracollect[B](pf: PartialFunction[B, A]): Ascend[R, B, E] =
      src => asc.adapt(src.collect(pf))

  inline def end[A, E]: Ascend[A, A, E] = identity
  inline def preceding[R, A, E](ascend: Ascend[R, A, E]): Ascend[R, A, E] = ascend

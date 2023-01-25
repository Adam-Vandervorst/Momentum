package be.adamv.momentum


@FunctionalInterface
trait Descend[-R, +A, E]:
  def adapt(s: Sink[A, E]): Sink[R, E]

  def map[B](f: A => B): Descend[R, B, E] =
    (s: Sink[B, E]) => adapt(a => s(f(a)))


trait DescendFactory[D[r, a, e] <: Descend[r, a, e]]:
  def start[A, E]: D[A, A, E]
  def succeeding[R, A, E](descend: Descend[R, A, E]): Descend[R, A, E]

object Descend extends DescendFactory[Descend]:
  inline def start[A, E]: Descend[A, A, E] = identity
  inline def succeeding[R, A, E](descend: Descend[R, A, E]): Descend[R, A, E] = descend


/*
  def buffered: SetAdaptor[A, R] & RBuffered[A] = new SetAdaptor[A, R] with ConcreteBuffered[A]:
    def apply(sset: Setter[A, _]): R =
      sadapt(a => {
        if last.fold(true)(_ != a) then sset(a)
        _last = Some(a)
      })

  def valued(initial: A): SetAdaptor[A, R] & RValued[A] = new SetAdaptor[A, R] with ConcreteValued[A](initial):
    def apply(sset: Setter[A, _]): R =
      sadapt(a => {
        sset(a)
        _value = a
      })

  def traced: SetAdaptor[A, R] & RTraced[A] = new SetAdaptor[A, R] with ConcreteTraced[A]:
    def apply(sset: Setter[A, _]): R =
      sadapt(a => {
        if last.fold(true)(_ != a) then sset(a)
        trace.push(a)
      })
*/
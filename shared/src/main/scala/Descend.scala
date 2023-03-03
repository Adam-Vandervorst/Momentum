package be.adamv.momentum


@FunctionalInterface
trait Descend[-R, +A, E]:
  def adapt(s: Sink[A, E]): Sink[R, E]

  def map[B](f: A => B): Descend[R, B, E] =
    (snk: Sink[B, E]) => adapt(a => snk.set(f(a)))

  def tapEach(f: A => Unit): Descend[R, A, E] =
    (snk: Sink[A, E]) => adapt(a => { f(a); snk.set(a) })


trait DescendFactory:
  def start[A, E]: Descend[A, A, E]
  def succeeding[R, A, E](descend: Descend[R, A, E]): Descend[R, A, E]

object Descend extends DescendFactory:
  extension [R, A, E] (dsc: Descend[R, A, E])(using Spawn[E])
    inline def filter(p: A => Boolean): Descend[R, A, E] =
      snk => dsc.adapt(snk.contrafilter(p))

    inline def collect[B](pf: PartialFunction[A, B]): Descend[R, B, E] =
      snk => dsc.adapt(snk.contracollect(pf))

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
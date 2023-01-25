package be.adamv.momentum


@FunctionalInterface
trait Sink[-A, E]:
  self =>
  def set(a: A): E

  def eachTapped[AA <: A](f: AA => Unit): Sink[AA, E] =
    (a: AA) =>
      f(a)
      set(a)

  def contramapTo[B](a: => A): Sink[B, E] =
    (b: B) => set(a)

  def contramap[B](f: B => A): Sink[B, E] =
    (b: B) => set(f(b))

  def scan[B, AA <: A](z: AA)(op: (AA, B) => AA): Sink[B, E] =
    var state: AA = z
    set(state)
    (b: B) =>
      state = op(state, b)
      set(state)

extension [T, S <: String & Singleton, A <: Tags.Value[T, S], E](snk: Sink[Tags.Value[T, S] *: EmptyTuple, E])
  inline def asSingle: Sink[T, E] = t => snk.set(Tags.name[S](t) *: EmptyTuple)

extension [A, E] (snk: Sink[A, E])(using d: Spawn[E])
  def contrafilter[AA <: A](f: AA => Boolean): Sink[AA, E] =
    (a: AA) => if f(a) then snk.set(a) else d.spawn()

  def contracollect[B](pf: PartialFunction[B, A]): Sink[B, E] =
    { case pf(a) => snk.set(a); case _ => d.spawn() }


/*
object Sink:
  extension [A, E](sset: Setter[A, E])
    def buffered: Setter[A, E] & RBuffered[A] = new Setter[A, E] with ConcreteBuffered[A]:
      def apply(a: A): E =
        val e = sset(a)
        _last = Some(a)
        e

    def valued(initial: A): Setter[A, E] & RValued[A] = new Setter[A, E] with ConcreteValued[A](initial):
      def apply(a: A): E =
        val e = sset(a)
        _value = a
        e

    def traced: Setter[A, E] & RTraced[A] = new Setter[A, E] with ConcreteTraced[A]:
      def apply(a: A): E =
        val e = sset(a)
        trace.push(a)
        e
*/
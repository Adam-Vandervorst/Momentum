package be.adamv.momentum


type Setter[-A, E] = A => E

extension [A, E](set: A => E)
  inline def eachTapped[AA <: A](f: AA => Unit): Setter[AA, E] =
    (a: AA) =>
      f(a)
      set(a)

  inline def contramapTo[B](a: => A): Setter[B, E] =
    (b: B) => set(a)

  inline def contramap[B](f: B => A): Setter[B, E] =
    (b: B) => set(f(b))

  inline def scan[B, AA <: A](z: AA)(op: (AA, B) => AA): Setter[B, E] =
    var state: AA = z
    set(state)
    (b: B) =>
      state = op(state, b)
      set(state)


extension [A, E](sset: Setter[A, E])(using inline d: Default[E])
  inline def contrafilter[AA <: A](f: AA => Boolean): Setter[AA, E] =
    (a: AA) => if f(a) then sset(a) else d.value

  inline def contracollect[B](pf: PartialFunction[B, A]): Setter[B, E] =
    { case pf(a) => sset(a); case _ => d.value }


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
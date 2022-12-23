package be.adamv.momentum


@FunctionalInterface
trait Sink[-A]:
  self =>
  // A Sink is defined solely by its side-effect
  def set(a: A): Unit

  inline def eachTapped[AA <: A](f: AA => Unit): Sink[AA] =
    (a: AA) =>
      f(a)
      self.set(a)

  inline def contramapTo[B](a: => A): Sink[B] =
    (b: B) => self.set(a)

  inline def contramap[B](f: B => A): Sink[B] =
    (b: B) => self.set(f(b))

  inline def contrafilter[AA <: A](f: AA => Boolean): Sink[AA] =
    (a: AA) => if f(a) then self.set(a)

  inline def contracollect[B](f: PartialFunction[B, A]): Sink[B] =
    (b: B) => f.unapply(b).foreach(self.set)

  inline def scan[B, AA <: A](z: AA)(op: (AA, B) => AA): Sink[B] =
    var state: AA = z
    self.set(state)
    (b: B) =>
      state = op(state, b)
      self.set(state)


object Sink:
  extension [A](es: Sink[A])
    def buffered: Sink[A] & RBuffered[A] = new Sink[A] with ConcreteBuffered[A]:
      def set(a: A): Unit =
        es.set(a)
        _last = Some(a)

    def valued(initial: A): Sink[A] & RValued[A] = new Sink[A] with ConcreteValued[A](initial):
      def set(a: A): Unit =
        es.set(a)
        _value = a

    def traced: Sink[A] & RTraced[A] = new Sink[A] with ConcreteTraced[A]:
      def set(a: A): Unit =
        es.set(a)
        trace.push(a)

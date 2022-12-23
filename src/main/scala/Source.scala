package be.adamv.momentum


// values are pushed though the system into the sink you specify
@FunctionalInterface
trait Source[+A]:
  self =>
  // A Source is defined by how you'd drain it (and how that affects the Source)
  def adapt(s: Sink[A]): Source[_]

  inline def tapEach(f: A => Unit): Source[A] =
    (s: Sink[A]) => self.adapt(s.eachTapped(f))

  inline def mapTo[B](v: => B): Source[B] =
    (s: Sink[B]) => self.adapt(s.contramapTo(v))

  inline def map[B](f: A => B): Source[B] =
    (s: Sink[B]) => self.adapt(s.contramap(f))

  inline def filter(p: A => Boolean): Source[A] =
    (s: Sink[A]) => self.adapt(s.contrafilter(p))

  inline def collect[B](pf: PartialFunction[A, B]): Source[B] =
    (s: Sink[B]) => self.adapt(s.contracollect(pf))

  inline def scan[B](z: B)(op: (B, A) => B): Source[B] =
    (s: Sink[B]) => self.adapt(s.scan(z)(op))

object Source:
  extension [A](es: Source[A])
    def register(f: A => Unit): Source[_] = es.adapt(a => f(a))

    def buffered: Source[A] & RBuffered[A] = new Source[A] with ConcreteBuffered[A]:
      def adapt(s: Sink[A]): Source[_] =
        es.adapt(a => {
          if last.fold(true)(_ != a) then s.set(a)
          _last = Some(a)
        })

    def valued(initial: A): Source[A] & RValued[A] = new Source[A] with ConcreteValued[A](initial):
      def adapt(s: Sink[A]): Source[_] =
        es.adapt(a => {
          s.set(a)
          _value = a
        })

    def traced: Source[A] & RTraced[A] = new Source[A] with ConcreteTraced[A]:
      def adapt(s: Sink[A]): Source[_] =
        es.adapt(a => {
          if last.fold(true)(_ != a) then s.set(a)
          trace.push(a)
        })

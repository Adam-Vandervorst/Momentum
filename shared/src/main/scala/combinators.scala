package be.adamv.momentum


object Tags:
  opaque type Value[+T, S <: String & Singleton] = T

  import scala.language.implicitConversions

  inline given [T, S <: String & Singleton]: Conversion[Value[T, S], T] = identity
  inline given [T, S <: String & Singleton]: Conversion[T, Value[T, S]] = identity

  extension (s: String)
    def ->(t: Any): Value[t.type, s.type] = t
  def name[S <: String & Singleton](t: Any): Value[t.type, S] = t


extension [A, E](self: Sink[A, E])
  infix def setBoth[B](other: Sink[B, E]): Sink[(A, B), E] =
    (p: (A, B)) =>
      self.set(p._1)
      other.set(p._2)


extension [A] (self: Descend[Unit, A, Unit])
  // should be called pairOnLeftWhenBufferedRight
  infix def zipLeft[B](other: Descend[Unit, B, Unit]): Descend[Unit, (A, B), Unit] =
    (s: Sink[(A, B), Unit]) =>
      var mb = Option.empty[B]
      val os = other.adapt(b => mb = Some(b))
      val r = self.adapt(a => if mb.nonEmpty then s.set(a, mb.get) else ())
      (r setBoth os).expanded

//extension [A, E] (self: Producer[A, E] & RBuffered[A])(using d: Default[E])
//  inline infix def zipRight[B](other: Producer[B, E]): Producer[(A, B), E] =
//    (sset: Sink[(A, B), E]) =>
//      self(a => d.value)
//      other(b => { if self.last.nonEmpty then sset(self.last.get, b) else d.value })

/*
extension [A](self: Producer[A])
  infix def combineLeftBuffered[B](other: Producer[B]): Producer[(A, B)] & RBuffered[(A, B)] = new Producer[(A, B)] with ConcreteBuffered[(A, B)]:
    var l: A = _
    var r: B = _
    var active = false

    override def last: Option[(A, B)] =
      if active then Some(l, r)
      else None

    override def adapt(s: Sink[(A, B)]): Unit =
      other.register(b =>
        r = b
        active = true
      )
      self.adapt(a =>
        l = a
        if active then
          s.set((l, r)))

  infix def combineLeft[B](other: Producer[B] & RBuffered[B]): Producer[(A, B)] =
    (s: Sink[(A, B)]) =>
      self.adapt(a => other.last.foreach(b => s.set(a, b)))

  infix def combineLeftWith[B, C](f: (A, B) => C)(other: Producer[B] & RBuffered[B]): Producer[C] =
    (s: Sink[C]) =>
      self.adapt(a => other.last.foreach(b => s.set(f(a, b))))

  infix def onLeft[B](other: Producer[B] & RBuffered[B]): Producer[B] =
    (s: Sink[B]) =>
      self.adapt(_ => other.last.foreach(b => s.set(b)))

extension [A](self: Producer[A] & RBuffered[A])
  infix def combine[B](other: Producer[B] & RBuffered[B]): Producer[(A, B)] =
    (s: Sink[(A, B)]) =>
      self.adapt(a => other.last.foreach(b => s.set(a, b)))
      other.adapt(b => self.last.foreach(a => s.set(a, b)))

  infix def combineRight[B](other: Producer[B]): Producer[(A, B)] =
    (s: Sink[(A, B)]) =>
      other.adapt(b => self.last.foreach(a => s.set(a, b)))
*/

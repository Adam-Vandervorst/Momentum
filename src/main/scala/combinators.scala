package be.adamv.momentum


/*
extension [A](self: Source[A])
  infix def combineLeftBuffered[B](other: Source[B]): Source[(A, B)] & RBuffered[(A, B)] = new Source[(A, B)] with ConcreteBuffered[(A, B)]:
    var l: A = _
    var r: B = _
    var active = false

    override def last: Option[(A, B)] =
      if active then Some(l, r)
      else None

    override def adapt(s: Sink[(A, B)]): Source[_] =
      other.register(b =>
        r = b
        active = true
      )
      self.adapt(a =>
        l = a
        if active then
          s.set((l, r))
      )

  infix def combineLeft[B](other: Source[B] & RBuffered[B]): Source[(A, B)] =
    (s: Sink[(A, B)]) =>
      self.adapt(a => other.last.foreach(b => s.set(a, b)))

  infix def combineLeftWith[B, C](f: (A, B) => C)(other: Source[B] & RBuffered[B]): Source[C] =
    (s: Sink[C]) =>
      self.adapt(a => other.last.foreach(b => s.set(f(a, b))))

  infix def onLeft[B](other: Source[B] & RBuffered[B]): Source[B] =
    (s: Sink[B]) =>
      self.adapt(_ => other.last.foreach(b => s.set(b)))

extension [A](self: Source[A] & RBuffered[A])
  infix def combine[B](other: Source[B] & RBuffered[B]): Source[(A, B)] =
    (s: Sink[(A, B)]) =>
      self.adapt(a => other.last.foreach(b => s.set(a, b)))
      other.adapt(b => self.last.foreach(a => s.set(a, b)))

  infix def combineRight[B](other: Source[B]): Source[(A, B)] =
    (s: Sink[(A, B)]) =>
      other.adapt(b => self.last.foreach(a => s.set(a, b)))
*/

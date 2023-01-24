package be.adamv.momentum

object macros:
  import scala.quoted.{Type, Quotes, Expr}
  def showTImpl[T : Type](using q: Quotes): Expr[String] =
    import q.reflect.{TypeTree, TypeRepr}
    Expr(TypeRepr.of[T].show)

object Tags:
  import scala.compiletime.{constValue, erasedValue}

  opaque type Ref[T, S <: String & Singleton] = S
  opaque type Value[+T, S <: String & Singleton] = T

  inline def require[T, S <: String & Singleton]: Ref[T, S] = constValue[S]
  inline def provide[T, S <: String & Singleton](t: T): Value[T, S] = t

//  extension[T, S <: String & Singleton] (ref: Ref[T, S])
//    inline def fill(t: T): Value[T, S] = provide(t)


//  inline def provideTup[T, S <: String & Singleton](t: T): Value[T, S] *: EmptyTuple = t *: EmptyTuple

  //  inline def instantiate[T, S <: String & Singleton](v: Value[T, S]): Unit = ()

//  inline def transform[Tup <: Tuple](tset: Tup): TSet[Tup, Tuple.Union[Tup]] = tset

//  inline def feedAll[S1 <: String & Singleton, S2 <: String & Singleton](
//                                                                          tup: (Value[Any, S1], Value[Any, S2])): Map[String, Any] = inline tup match
//    case (v1, v2) => Map(constValue[S1] -> v1, constValue[S2] -> v2)
//  inline def feedAll[S1 <: String & Singleton, S2 <: String & Singleton, S3 <: String & Singleton](
//                                                                                                    tup: (Value[Any, S1], Value[Any, S2], Value[Any, S3])): Map[String, Any] = inline tup match
//    case (v1, v2, v3) => Map(constValue[S1] -> v1, constValue[S2] -> v2, constValue[S3] -> v3)

//  inline def allTags[S1 <: String & Singleton]: List[String] = inline erasedValue[] match
//    case (v1, v2) => Map(constValue[S1] -> v1, constValue[S2] -> v2)
//  inline def feedAll[S1 <: String & Singleton, S2 <: String & Singleton](
//                                                                          tup: (Value[Any, S1], Value[Any, S2])): Map[String, Any] = inline tup match
//    case (v1, v2) => Map(constValue[S1] -> v1, constValue[S2] -> v2)
//
//  inline def feedAll[S1 <: String & Singleton, S2 <: String & Singleton, S3 <: String & Singleton](
//                                                                                                    tup: (Value[Any, S1], Value[Any, S2], Value[Any, S3])): Map[String, Any] = inline tup match
//    case (v1, v2, v3) => Map(constValue[S1] -> v1, constValue[S2] -> v2, constValue[S3] -> v3)


extension [A, E](self: Sink[A, E])
  infix def setBoth[B](other: Sink[B, E]): Sink[(A, B), E] =
    (p: (A, B)) =>
      self(p._1)
      other(p._2)


//extension [A, E] (self: Producer[A, E])(using d: Default[E])
//  infix def zipLeft[B](other: Producer[B, E] & RBuffered[B]): Producer[(A, B), E] =
//    (sset: Sink[(A, B), E]) =>
//      other(b => d.value)
//      self(a => { if other.last.nonEmpty then sset(a, other.last.get) else d.value })
//
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

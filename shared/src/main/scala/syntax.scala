package be.adamv.momentum


extension [E](inline ticker: Sink[Unit, E])
  inline def tick(): E = ticker.set(())

extension [A](inline getter: Source[A, Unit])
  inline def value: A = getter.get(())

extension [A, E](inline simple: Descend[Unit, A, E])
  inline def adaptNow(snk: Sink[A, E]): E = simple.adapt(snk).tick()

extension [A, R](inline simple: Ascend[R, A, Unit])
  inline def adaptNow(src: Source[A, Unit]): R = simple.adapt(src).value


extension [A](inline place: Source[A, Unit] & Sink[A, Unit])
  inline def update(inline f: A => A): Unit =
    place.set(f(place.value))
  inline def updateOptional(inline f: A => Option[A]): Unit = f(place.value) match
    case Some(rv) => place.set(rv)
    case None => ()

extension [A](inline place: Source[Option[A], Unit] & Sink[A, Unit])
  inline def updatePresent(inline f: A => A): Unit = place.value match
    case Some(v) => place.set(f(v))
    case None => ()
  inline def updatePresentOptional(inline f: A => Option[A]): Unit = place.value match
    case Some(v) => f(v) match
      case Some(rv) => place.set(rv)
      case None => ()
    case None => ()


//extension [X, R](sadapt: Producer[X, R])
//  transparent inline def -->(s: Sink[X, R]): R =
//    sadapt(s)

package be.adamv.momentum


extension [E](inline ticker: Sink[Unit, E])
  inline def tick(): E = ticker.set(())

extension [A](inline getter: Source[A, Unit])
  inline def value: A = getter.get(())

extension [A, E](inline simple: Descend[Unit, A, E])
  inline def adaptNow(snk: Sink[A, E]): E = simple.adapt(snk).tick()

extension [A, R](inline simple: Ascend[R, A, Unit])
  inline def adaptNow(src: Source[A, Unit]): R = simple.adapt(src).value


//extension [X, R](sadapt: Producer[X, R])
//  transparent inline def -->(s: Sink[X, R]): R =
//    sadapt(s)

package be.adamv.momentum


extension (ticker: Sink[Unit])
  def tick(): Unit = ticker.set(())


extension [S[X] <: Source[X], X](es: S[X])
  transparent inline def -->(s: Sink[X]) =
    es.adapt(s)

extension [S[X] <: Sink[X], X](es: S[X])
  transparent inline def <--(s: Source[X]): S[X] =
    s.adapt(es)
    es

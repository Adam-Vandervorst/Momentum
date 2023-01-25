package be.adamv.momentum


extension (ticker: Sink[Unit, _])
  def tick(): Unit = ticker.set(())

//
//extension [X, R](sadapt: Producer[X, R])
//  transparent inline def -->(s: Sink[X, R]): R =
//    sadapt(s)

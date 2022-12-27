package be.adamv.momentum


extension (ticker: Setter[Unit, _])
  def tick(): Unit = ticker(())


extension [X, R](sadapt: SetAdaptor[X, R])
  transparent inline def -->(s: Setter[X, R]): R =
    sadapt(s)

package be.adamv.momentum


type SetAdaptor[+A, R] = Setter[A, R] => R

extension [A, R](sadapt: SetAdaptor[A, R])
  inline def mapS[B](inline f: Setter[B, R] => Setter[A, R]): SetAdaptor[B, R] =
    (s: Setter[B, R]) => sadapt(f(s))

  /*
  def buffered: SetAdaptor[A, R] & RBuffered[A] = new SetAdaptor[A, R] with ConcreteBuffered[A]:
    def apply(sset: Setter[A, _]): R =
      sadapt(a => {
        if last.fold(true)(_ != a) then sset(a)
        _last = Some(a)
      })

  def valued(initial: A): SetAdaptor[A, R] & RValued[A] = new SetAdaptor[A, R] with ConcreteValued[A](initial):
    def apply(sset: Setter[A, _]): R =
      sadapt(a => {
        sset(a)
        _value = a
      })

  def traced: SetAdaptor[A, R] & RTraced[A] = new SetAdaptor[A, R] with ConcreteTraced[A]:
    def apply(sset: Setter[A, _]): R =
      sadapt(a => {
        if last.fold(true)(_ != a) then sset(a)
        trace.push(a)
      })
*/
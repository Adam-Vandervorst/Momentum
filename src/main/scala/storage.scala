package be.adamv.momentum


trait RBuffered[A]:
  def last: Option[A] = None

trait WBuffered[A]:
  def newValue(a: A): Unit

trait ConcreteBuffered[A] extends RBuffered[A], WBuffered[A]:
  protected var _last: Option[A] = None
  override def last: Option[A] = _last
  override def newValue(a: A): Unit = _last = Some(a)

trait RValued[A] extends RBuffered[A]:
  def value: A
  override def last: Option[A] = Some(value)

trait WValued[A] extends WBuffered[A]:
  def value_=(value: A): Unit
  override def newValue(a: A): Unit = value_=(a)

trait ConcreteValued[A](protected var _value: A) extends RValued[A], WValued[A]:
  def value: A = _value
  def value_=(v: A): Unit = _value = v

trait RTraced[A] extends RBuffered[A]:
  override def last: Option[A] = history(0)
  def history(i: Int): Option[A]

trait WTraced[A] extends RBuffered[A]

trait ConcreteTraced[A] extends RTraced[A], WTraced[A]:
  val trace: collection.mutable.Stack[A] = collection.mutable.Stack.empty

  override def last: Option[A] = trace.headOption
  def history(i: Int): Option[A] = Option.when(i < trace.length)(trace(i))
  def newValue(a: A): Unit = trace.push(a)

/*extension [A](es: RBuffered[A] & Source[A])
  def dedup: RBuffered[A] & Source[A] = new Source[A] with ConcreteBuffered[A]:
    override def adapt(s: Sink[A]): Source[_] =
      es.adapt(a => if a != es.last then s.set(a))

extension [A](es: RBuffered[A] & Sink[A])
  def onNew: RBuffered[A] & Sink[A] = new Sink[A] with ConcreteBuffered[A]:
    override def set(a: A): Unit =
      if es.last.fold(true)(_ != a) then es.set(a)
  def setBefore(a: A)(f: A => Unit): Unit =
    val v = es.last
    es.set(a)
    v.foreach(f)
  def setAfter(a: A)(f: A => Unit): Unit =
    es.last.foreach(f)
    es.set(a)
  def updateWith(f: Option[A] => Option[A]): Unit = f(es.last).foreach(es.set)

extension [A](es: RValued[A] & Sink[A])
  def update(f: A => A): Unit = es.set(f(es.value))


extension[A] (es: Source[A])
  def register(f: A => Unit): Source[_] = es.adapt(a => f(a))

  def buffered: RBuffered[A] & Source[A] = new Source[A] with ConcreteBuffered[A]:
    def adapt(s: Sink[A]): Source[_] =
      es.adapt(a => {
        if last.fold(true)(_ != a) then s.set(a)
        newValue(a)
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
        newValue(a)
      })*/

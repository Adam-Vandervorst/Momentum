package be.adamv.momentum
package concrete


// In-order buffered
class Relay[A] extends Sink[A, Unit], Descend[Unit, A, Unit], Source[Option[A], Unit]:
  self =>
  private var last = Option.empty[A]
  private val subs = collection.mutable.Stack.empty[Sink[A, Unit]]

  override def adapt(s: Sink[A, Unit]): Sink[Unit, Unit] =
    u => subs.push(s)

  override def set(a: A): Unit =
    last = Some(a)
    subs.foreach(_.set(a))

  override def get(e: Unit): Option[A] =
    last

  override def map[B](f: A => B): Descend[Unit, B, Unit] & Source[Option[B], Unit] = new Descend[Unit, B, Unit] with Source[Option[B], Unit]:
    def adapt(sink: Sink[B, Unit]): Sink[Unit, Unit] = self.adapt((a: A) => sink.set(f(a)))
    override def get(e: Unit): Option[B] = self.last.map(f)


object Relay:
  inline def start[A]: Relay[A] = new Relay[A] {}

  inline def succeeding[R, A, E](descend: Descend[R, A, E]): Descend[R, A, E] = descend


  //  def inOrderDrop[V](srcs: List[Source[V]]): RBuffered[V] & Source[V] =
//    new Relay[V]:
//      private val l = srcs.length
//      private var k = 0
//      for (src, i) <- srcs.zipWithIndex do
//        src.register(v => {
//          if i == k then
//            set(v)
//            k = (k + 1) % l
//        })

  //    def joinLeft[K, V](srcs: List[Source[(K, V)]]): Source[V] =
  //      new Relay[V]:
  //        val lastK: Array[Option[K]] = Array.fill(srcs.length)(None)
  //        for (src, i) <- srcs.zipWithIndex do
  //          src.register((k, v) => {
  //            lastK(i) = Some(k)
  //          })

//  def inOrderGen[V](srcs: Source[(Int, V)]*): RBuffered[V] & Source[V] =
//    new Relay[V] :
//      private var k = 0
//      private var wave = -1
//      for (src, i) <- srcs.zipWithIndex do
//        src.register((nwave, v) => {
//          if nwave > wave then
//            set(v)
//            k = 0
//            wave = nwave
//          else if nwave == wave && i <= k then
//            set(v)
//            k = i
//        })


package be.adamv.momentum
package concrete


class InstantRelay[A] extends Sink[A, Unit], Descend[Unit, A, Unit]:
  self =>
  private val subs = collection.mutable.Stack.empty[Sink[A, Unit]]

  override def adapt(s: Sink[A, Unit]): Sink[Unit, Unit] =
    u => subs.push(s)

  override def set(a: A): Unit =
    subs.foreach(_.set(a))


object InstantRelay:
  inline def start[A]: InstantRelay[A] = new InstantRelay[A] {}


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


package be.adamv.momentum


@FunctionalInterface
trait Source[+A, E]:
  self =>
  def get(e: E): A

  def tapEach(f: A => Unit): Source[A, E] =
    e =>
      val a = self.get(e)
      f(a)
      a

  def map[B](f: A => B): Source[B, E] =
    e => f(self.get(e))

object Source:
  extension[A, E](src: Source[A, E])(using c: Cease[E])
    def mapTo[B](v: => B): Source[B, E] =
      e =>
        c.cease(e)
        v

  extension[A, E](src: Source[A, E])(using d: Duplicate[E])
    inline def filter(p: A => Boolean): Source[A, E] =
      initial =>
        val (ine, ie) = d.duplicate(initial)
        var a = src.get(ie)
        var re = ine
        while !p(a) do
          val (ne, e) = d.duplicate(re)
          re = ne
          a = src.get(e)
        a

    inline def collect[B](pf: PartialFunction[A, B]): Source[B, E] =
      @annotation.tailrec
      def rec(ie: E): B =
        val (ne, e) = d.duplicate(ie)
        src.get(e) match
          case pf(b) => b
          case _ => rec(ne)
      rec

//  def scan[B](z: B)(op: (B, A) => B): Source[B, E] =
//    var state: B = z
//    e =>
//      val a = src.get(e)
//      state = op(state, a)
//      state
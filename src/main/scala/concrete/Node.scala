package be.adamv.momentum
package concrete

class Node[R, A, E] extends Descend[R, A, E]:
  self =>
  val index: Int = 0

  override def adapt(s: Sink[A, E]): Sink[R, E] = ???

  override def map[B](f: A => B): Node[R, B, E] = new:
    override val index: Int = self.index + 1

    override def adapt(s: Sink[B, E]): Sink[R, E] =
      self.adapt((a: A) => s(f(a)))

  def parallel[S, B, F[_, _], CF[_, _]](
    op: (A, B) => F[A, B],
    coop: CF[R, S] => (R, S))(
    other: Node[S, B, E])(
    using d: Default[E], m: Merge[E],
          ): Node[CF[R, S], F[A, B], E] = new:
    override val index: Int = (self.index max other.index) + 1

    override def adapt(s: Sink[F[A, B], E]): Sink[CF[R, S], E] =
      if self.index > other.index then
        println(s"here ${self.index} ${other.index}")
        var vb: Option[B] = None
        val others = other.adapt(b => {vb = Some(b); d.value})
        val selfs = self.adapt(a => s(op(a, vb.get)))
        t =>
          val (r, s) = coop(t)
          {val o = others(s); m.merge(selfs(r), o)}
      else
        println(s"there ${self.index} ${other.index}")
        var va: Option[A] = None
        val selfs = self.adapt(a => {va = Some(a); d.value})
        val others = other.adapt(b => s(op(va.get, b)))
        t =>
          val (r, s) = coop(t)
          m.merge(selfs(r), others(s))


  def mergeWith[S, B, C, T](op: (A, B) => C, coop: T => (R, S))(other: Node[S, B, E])(using d: Default[E], m: Merge[E]): Node[T, C, E] = new:
    override val index: Int = (self.index max other.index) + 1

    override def adapt(s: Sink[C, E]): Sink[T, E] =
      if self.index > other.index then
        println(s"here ${self.index} ${other.index}")
        var vb: Option[B] = None
        val others = other.adapt(b => {vb = Some(b); d.value})
        val selfs = self.adapt(a => s(op(a, vb.get)))
        t =>
          val (r, s) = coop(t)
          {val o = others(s); m.merge(selfs(r), o)}
      else
        println(s"there ${self.index} ${other.index}")
        var va: Option[A] = None
        val selfs = self.adapt(a => {va = Some(a); d.value})
        val others = other.adapt(b => s(op(va.get, b)))
        t =>
          val (r, s) = coop(t)
          m.merge(selfs(r), others(s))

  inline infix def merge[S, B](other: Node[S, B, E])(using Default[E], Merge[E]): Node[(R, S), (A, B), E] =
    self.mergeWith[S, B, (A, B), (R, S)](Tuple2.apply, identity)(other)

  import compiletime.{constValue, constValueTuple, erasedValue}
  import compiletime.ops.int.S
  import compiletime.ops.any.{==, !=}
  import Tags.Value
  type AssumeTuple[X] <: Tuple = X match
//    case Value[t, s] => X *: EmptyTuple
    case Value[t, s] => Tuple1[X]
    case Tuple1[t1] => Tuple1[t1]
    case (t1, t2) => (t1, t2)
    case (t1, t2, t3) => (t1, t2, t3)
  type TagOf[V] <: String = V match
    case Value[_, s] => s
  type IndexWhere[Tup <: Tuple, P[_] <: Boolean, Pos <: Int] <: Int = Tup match
    case EmptyTuple => Pos
    case *:[h, t] => P[h] match
      case true => Pos
      case false => IndexWhere[t, P, S[Pos]]
  type FilterOnIndex[Tup <: Tuple, P[_ <: Tuple.Union[Tup], _ <: Int] <: Boolean, Pos <: Int] <: Tuple = Tup match
    case EmptyTuple => EmptyTuple
    case *:[h, t] => P[h, Pos] match
      case true => h *: FilterOnIndex[t, P, S[Pos]]
      case false => FilterOnIndex[t, P, S[Pos]]
  type Dedup[Tup <: Tuple] = FilterOnIndex[Tup, [X, I] =>> I == IndexWhere[Tup, [Y] =>> X == Y, -1], 0]
  type MergeTuple[x, y] = Dedup[Tuple.Concat[AssumeTuple[x], AssumeTuple[y]]]
  inline def filterOnType[Tup <: Tuple, P[_] <: Boolean](tup: Tup): Tuple.Filter[Tup, P] = inline tup match
    case EmptyTuple => EmptyTuple.asInstanceOf
    case c: (*:[ht, tt]) => inline if constValue[P[ht]]
      then (c.head *: filterOnType[tt, P](c.tail)).asInstanceOf
      else filterOnType[tt, P](c.tail).asInstanceOf

  inline def tags[Tup <: Tuple]: List[String] = inline erasedValue[Tup] match
    case _: EmptyTuple => Nil
    case _: Tuple1[Value[_, s0]] => constValue[s0]::Nil
//    case _: Tuple2[Value[_, s0], Value[_, s1]] => constValue[s0]::constValue[s1]::Nil
//    case _: Tuple3[Value[_, s0], Value[_, s1], Value[_, s2]] => constValue[s0]::constValue[s1]::constValue[s2]::Nil
//    case _: (*:[Value[_, s], tt]) => constValue[s] :: tags[tt]

  inline infix def smartMerge[S, B](other: Node[S, B, E])(using Default[E], Merge[E]): Node[MergeTuple[R, S], (A, B), E] =
    self.parallel[S, B, [a, b] =>> (a, b), [r, s] =>> MergeTuple[r, s]](Tuple2.apply, { c =>
//      println(s"c: ${c}, tags: ${tags[Tuple1[Value[Int, "t1"]]]}")
      ???
//      (filterOnType[MergeTuple[R, S], [X] =>> -1 != IndexWhere[AssumeTuple[R], [Y] =>> X == Y, -1]](c).asInstanceOf[R],
//        filterOnType[MergeTuple[R, S], [X] =>> -1 != IndexWhere[AssumeTuple[S], [Y] =>> X == Y, -1]](c).asInstanceOf[S])
    })(other)


object Node extends DescendFactory[Node]:
  override def start[A, E]: Node[A, A, E] = new Node:
    override def adapt(s: Sink[A, E]): Sink[A, E] = s

  override def continuing[R, A, E](descend: Descend[R, A, E]): Node[R, A, E] = new Node:
    override def adapt(s: Sink[A, E]): Sink[R, E] = descend.adapt(s)

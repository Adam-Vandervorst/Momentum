package be.adamv.momentum
package concrete


import compiletime.{constValue, constValueTuple, erasedValue}
import compiletime.ops.int.S
import compiletime.ops.any.{==, !=}
import Tags.Value


// TODO hack
type AssumeTuple[X] = X & Tuple
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
type Dedup[Tup <: Tuple] = FilterOnIndex[Tup, [X, I <: Int] =>> I == IndexWhere[Tup, [Y] =>> TagOf[X] == TagOf[Y], 0], 0]
// TODO Tuple.Map with identity is a hack to force evaluation
type MergeTuple[x <: Tuple, y <: Tuple] = Dedup[Tuple.Concat[x, y]]

// TODO the following three functions could be merge, no need for the intermediate key lists
inline def tags[Tup <: Tuple]: List[String] = inline erasedValue[Tup] match
  case _: EmptyTuple => Nil
  case _: *:[v, EmptyTuple] => constValue[TagOf[v]] :: Nil
  case _: *:[v1, *:[v2, EmptyTuple]] => constValue[TagOf[v1]]::constValue[TagOf[v2]] :: Nil
  case _: *:[v1, *:[v2, *:[v3, EmptyTuple]]] => constValue[TagOf[v1]]::constValue[TagOf[v2]]::constValue[TagOf[v3]] :: Nil

// TODO this could be done at compiletime for 90%
def distribute(c: Tuple, combined: List[String], left: List[String], right: List[String],
               l: Tuple = EmptyTuple, r: Tuple = EmptyTuple): (Tuple, Tuple) = combined match
  case Nil =>
    assert(c.size == 0)
    assert(left.isEmpty && right.isEmpty)
    (l, r)
  case h :: t =>
    // TODO simplify
    distribute(c.drop(1), t,
      if left.headOption.contains(h) then left.tail else left,
      if right.headOption.contains(h) then right.tail else right,
      if left.headOption.contains(h) then l :* c.asInstanceOf[NonEmptyTuple].head else l,
      if right.headOption.contains(h) then r :* c.asInstanceOf[NonEmptyTuple].head else r)

inline def splitTuple[R <: Tuple, S <: Tuple](c: MergeTuple[R, S]): (R, S) =
//  println(s"c: ${c}, tags: ${tags[MergeTuple[R, S]]}, ${tags[R]}, ${tags[S]}")
//      println(distribute(c, tags[MergeTuple[R, S]], tags[LiftTuple[R]], tags[LiftTuple[S]]))
//  val (l, r) = distribute(c, tags[MergeTuple[R, S]], tags[R], tags[S]).asInstanceOf[(R, S)]
//  println(s"l: ${l}, r: ${r}")
  distribute(c, tags[MergeTuple[R, S]], tags[R], tags[S]).asInstanceOf[(R, S)]


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
//        println(s"here ${self.index} ${other.index}")
        var vb: Option[B] = None
        val others = other.adapt(b => {vb = Some(b); d.value})
        val selfs = self.adapt(a => s(op(a, vb.get)))
        t =>
          val (r, s) = coop(t)
          {val o = others(s); m.merge(selfs(r), o)}
      else
//        println(s"there ${self.index} ${other.index}")
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
//        println(s"here ${self.index} ${other.index}")
        var vb: Option[B] = None
        val others = other.adapt(b => {vb = Some(b); d.value})
        val selfs = self.adapt(a => s(op(a, vb.get)))
        t =>
          val (r, s) = coop(t)
          {val o = others(s); m.merge(selfs(r), o)}
      else
//        println(s"there ${self.index} ${other.index}")
        var va: Option[A] = None
        val selfs = self.adapt(a => {va = Some(a); d.value})
        val others = other.adapt(b => s(op(va.get, b)))
        t =>
          val (r, s) = coop(t)
          m.merge(selfs(r), others(s))

  inline infix def merge[S, B](other: Node[S, B, E])(using Default[E], Merge[E]): Node[(R, S), (A, B), E] =
    self.mergeWith[S, B, (A, B), (R, S)](Tuple2.apply, identity)(other)

extension [R <: Tuple, A, E](n: Node[R, A, E])
  inline infix def smartMerge[S <: Tuple, B](other: Node[S, B, E])(using Default[E], Merge[E]): Node[MergeTuple[R, S], (A, B), E] =
    n.parallel[S, B, Tuple2, [r, s] =>> MergeTuple[AssumeTuple[r], AssumeTuple[s]]](Tuple2(_, _), splitTuple(_))(other)


object Node extends DescendFactory[Node]:
  inline def named[A, E](n: String): Node[Value[A, n.type] *: EmptyTuple, A, E] = new Node:
    override def adapt(s: Sink[A, E]): Sink[Value[A, n.type] *: EmptyTuple, E] = s.contramap(_.head)

  override def start[A, E]: Node[A, A, E] = new Node:
    override def adapt(s: Sink[A, E]): Sink[A, E] = s

  override def continuing[R, A, E](descend: Descend[R, A, E]): Node[R, A, E] = new Node:
    override def adapt(s: Sink[A, E]): Sink[R, E] = descend.adapt(s)

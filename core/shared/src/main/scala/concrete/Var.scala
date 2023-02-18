package be.adamv.momentum
package concrete


class Var[A](initial: A) extends Place[A]:
  protected var value: A = initial

  def set(a: A): Unit = value = a
  def get(u: Unit): A = value

object Var:
  def entangled[A, B](a_b: Either[A, B], l: A => B, r: B => A): (Var[A], Var[B]) =
    lazy val va: Var[A] = new Var(a_b.fold(identity, r)):
      override def set(a: A): Unit =
        value = a
        vb.value = l(a)

    lazy val vb: Var[B] = new Var(a_b.fold(l, identity)):
      override def set(b: B): Unit =
        value = b
        va.value = r(b)
    (va, vb)


  inline def cone[C, A, B](inline initial: C,
      inline af: A => C, inline ab: C => A,
      inline bf: B => C, inline bb: C => B): (Place[A], Place[B]) =
    var c = initial

    val pa: Place[A] = new Place:
      override def set(a: A): Unit = c = af(a)
      override def get(e: Unit): A = ab(c)

    val pb: Place[B] = new Place:
      override def set(b: B): Unit = c = bf(b)
      override def get(e: Unit): B = bb(c)

    (pa, pb)

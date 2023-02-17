package be.adamv.momentum


abstract class Process[A, B] extends Descend[A, B, Unit], Ascend[B, A, Unit]
//  def adapt(s: Source[A, Unit]): Source[B, Unit] = ???
//  def adapt(s: Sink[B, Unit]): Sink[A, Unit] = ???

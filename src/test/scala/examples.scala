package be.adamv.momentum

import be.adamv.momentum.util.*
import be.adamv.momentum.concrete.*
import munit.FunSuite


class BaseExamples extends FunSuite:
  test("Var fibonacci") {
    val fib = Var(1, 1)
    val (pulse, progress) = newTicker()
    val (buffer, reset) = newTrace[Int]()

    fib <-- pulse.onLeft(fib).map((x, y) => (y, x + y))
    fib --> buffer.contramap(_._1)

    progress(10)
    assert(reset() == List(1, 2, 3, 5, 8, 13, 21, 34, 55, 89))
  }

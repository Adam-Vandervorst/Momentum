package be.adamv.momentum

import be.adamv.momentum.util.*
import be.adamv.momentum.concrete.*
import munit.FunSuite

type Token = String
type Temp = Int
type Cloudiness = Double
case class WeatherReport(temperature: Temp, cloudiness: Cloudiness)

class WeatherAPI extends Source[WeatherReport, Token]:
  private val valid_tokens: Set[Token] = Set("djweo", "j23oa")

  var temp = 20
  def genWeatherReport(): WeatherReport =
    temp += 1
    WeatherReport(temp, Math.random())

  override def get(token: Token): WeatherReport =
    if valid_tokens.contains(token) then genWeatherReport()
    else throw RuntimeException(f"Unauthorized $token")


class EmailReader[A](name: String) extends Sink[A, Unit]:
  override def set(a: A): Unit =
    println(f"$name reads email $a")


class WeatherEmail extends FunSuite:
  test("API company customer") {
    val api: Source[WeatherReport, Token] = WeatherAPI()

    val (hand, clock) = callback[Unit]()
    val api_call_server: InstantRelay[WeatherReport] = InstantRelay()
    clock.adaptNow(api_call_server.contramap(_ => api.get("j23oa")))
    val email_server: Descend[Unit, Temp, Unit] = api_call_server.map(_.temperature)

    val alice: Sink[Temp, Unit] = EmailReader[Temp]("Alice")
    val bob: Sink[Temp, Unit] = EmailReader[Temp]("Bob")

    val alice_subscribe = email_server.adapt(alice)
    val bob_subscribe = email_server.adapt(bob)

    alice_subscribe.tick()

    hand.tick()

    hand.tick()

    bob_subscribe.tick()

    hand.tick()

  }

class FibVariants extends FunSuite:
  ()
//  test("Var fibonacci") {
//    val fib = Var(1, 1)
//    val (pulse, progress) = newTicker()
//    val (buffer, reset) = newTrace[Int]()
//
//    fib <-- pulse.onLeft(fib).map((x, y) => (y, x + y))
//    fib --> buffer.contramap(_._1)
//
//    progress(10)
//    assert(reset() == List(1, 2, 3, 5, 8, 13, 21, 34, 55, 89))
//  }


/*
class FizzBuzzVariants extends FunSuite:
  val factor1 = 3
  val factor2 = 5

  val fizzbuzz: List[String] = for x <- List.range(1, 101) yield
    (x % factor1 == 0, x % factor2 == 0) match
      case (true, true) => "fizzbuzz"
      case (true, false) => "fizz"
      case (false, true) => "buzz"
      case (false, false) => x.toString

  test("fizzbuzz 1") {
    val (trace, result) = newTrace[String]()

    val counter = Var(0)

    val fizzes = counter.map(_ % factor1 == 0)
    val buzzes = counter.map(_ % factor2 == 0)

    trace <-- (counter combineLeftBuffered (fizzes combineLeftBuffered buzzes)).map{
      case (_, (true, true)) => "fizzbuzz"
      case (_, (true, false)) => "fizz"
      case (_, (false, true)) => "buzz"
      case (x, (false, false)) => x.toString
    }

    for _ <- 1 to 100 do counter.update(_ + 1)

    assert(result() == fizzbuzz)
  }

  test("fizzbuzz 2") {
    val (trace, result) = newTrace[String]()

    val counter = Var(0)

    val fizzbuzzes = counter.collect{ case x if x % factor1 == 0 && x % factor2 == 0 => x -> "fizzbuzz" }
    val fizzes = counter.collect{ case x if x % factor1 == 0 => x -> "fizz" }
    val buzzes = counter.collect{ case x if x % factor2 == 0 => x -> "buzz" }
    val counts = counter.map(x => x -> x.toString)

    trace <-- Relay.inOrderGen(fizzbuzzes, fizzes, buzzes, counts)

    for _ <- 1 to 100 do counter.update(_ + 1)

    assert(result() == fizzbuzz)
  }

//  test("fizzbuzz 3") {
//    val (trace, result) = newTrace[Any]()
//
//    val counter = Var(1)
//
//    val fizzes = counter.filter(_ % factor1 == 0).mapTo("fizz")
//    val buzzes = counter.filter(_ % factor2 == 0).mapTo("buzz")
//    val fizzbuzzes = (fizzes when buzzes).mapTo("fizzbuzz")
//    val counts = (counter unless (fizzes or buzzes)).map(_.toString)
//
//    trace <-- fizzes unless fizzbuzzes
//    trace <-- buzzes unless fizzbuzzes
//    trace <-- fizzbuzzes
//    trace <-- counts
//
//    for _ <- 1 to 100 do counter.update(_ + 1)
//
//    assert(result() == fizzbuzz)
//  }

*/

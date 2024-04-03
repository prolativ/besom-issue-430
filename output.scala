package repro

import scala.collection.BuildFrom
import scala.reflect.Typeable

trait Context

// dummy impl of Besom's Output
class Output[+A](val value: A) extends AnyVal:
  def map[B](f: A => B): Output[B] = Output(f(value))
  def flatMap[B](f: A => Output[B]): Output[B] = f(value)

  def get: A = value

object Output:
  // signature unchanged from Besom core
  def sequence[A, CC[X] <: Iterable[X], To](
      coll: CC[Output[A]]
  )(using bf: BuildFrom[CC[Output[A]], A, To], ctx: Context): Output[To] =
    Output(bf.fromSpecific(coll)(coll.map(_.value)))

// This is taken from Besom core verbatim
trait OutputExtensionsFactory:
  implicit final class OutputSequenceOps[A, CC[X] <: Iterable[X], To](coll: CC[Output[A]]):
    def sequence(using BuildFrom[CC[Output[A]], A, To], Context): Output[To] =
      Output.sequence(coll)

  implicit final class OutputTraverseOps[A, CC[X] <: Iterable[X]](coll: CC[A]):
    def traverse[B, To](f: A => Output[B])(using BuildFrom[CC[Output[B]], B, To], Context): Output[To] =
      coll.map(f).asInstanceOf[CC[Output[B]]].sequence

  implicit final class OutputOptionOps[A](output: Output[Option[A]]):
    def getOrElse[B >: A: Typeable](default: => B | Output[B])(using ctx: Context): Output[B] =
      output.flatMap { opt =>
        opt match
          case Some(a) => Output(a)
          case None =>
            default match
              case b: Output[B @unchecked] => b
              case b: B                    => Output(b)
      }
    def orElse[B >: A](alternative: => Option[B] | Output[Option[B]])(using ctx: Context): Output[Option[B]] =
      output.flatMap { opt =>
        opt match
          case some @ Some(_) => Output(some)
          case None =>
            alternative match
              case b: Output[Option[B]] => b
              case b: Option[B]         => Output(b)
      }

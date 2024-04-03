package repro

import scala.collection.BuildFrom

class Output[+A](val value: A)

object Output:
  def sequence[A, CC[X] <: Iterable[X], To](
      coll: CC[Output[A]]
  )(using bf: BuildFrom[CC[Output[A]], A, To]): Output[To] =
    Output(bf.fromSpecific(coll)(coll.map(_.value)))

trait OutputExtensionsFactory:
  implicit final class OutputSequenceOps[A, CC[X] <: Iterable[X], To](coll: CC[Output[A]]):
    def sequence(using BuildFrom[CC[Output[A]], A, To]): Output[To] =
      Output.sequence(coll)

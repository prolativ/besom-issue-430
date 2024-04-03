package repro

import munit.FunSuite

class Suite extends FunSuite:
  test("in main") {
    import inmain.*

    val ints = List(Output(1))
    ints.sequence
  }

  test("in test") {
    import intest.*

    val ints = List(Output(1))
    ints.sequence
  }

  test("local") {
    object OutputExtensions extends repro.OutputExtensionsFactory
    import OutputExtensions.*

    val ints = List(Output(1))
    ints.sequence
  }

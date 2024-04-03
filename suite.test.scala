package repro

import munit.FunSuite

class Suite extends FunSuite:
  test("in main") {
    import inmain.*
    import java.io.File

    object s3:
      def BucketObject(name: String)(using Context): Output[Unit] = Output(())

    given Context = new Context {}

    val uploads = File(".").listFiles().toList.traverse { file =>
      val name = Output(file.getName())

      name.flatMap(s3.BucketObject)
    }

    uploads.get
  }

  test("in test") {
    import intest.*
    import java.io.File

    object s3:
      def BucketObject(name: String)(using Context): Output[Unit] = Output(())

    given Context = new Context {}

    val uploads = File(".").listFiles().toList.traverse { file =>
      val name = Output(file.getName())

      name.flatMap(s3.BucketObject)
    }

    uploads.get
  }

  test("local") {
    object OutputExtensions extends repro.OutputExtensionsFactory
    import OutputExtensions.*

    import java.io.File
    object s3:
      def BucketObject(name: String)(using Context): Output[Unit] = Output(())

    given Context = new Context {}

    val uploads = File(".").listFiles().toList.traverse { file =>
      val name = Output(file.getName())

      name.flatMap(s3.BucketObject)
    }

    uploads.get
  }

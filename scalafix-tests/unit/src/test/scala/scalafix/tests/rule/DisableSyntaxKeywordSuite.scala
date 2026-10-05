package scalafix.tests.rule

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import org.scalatest.funsuite.AnyFunSuite
import scalafix.v1.Main

class DisableSyntaxKeywordSuite extends AnyFunSuite {

  test("forSome is reported when disabled (#2522)") {
    val dir = Files.createTempDirectory("scalafix-forsome")
    Files.write(
      dir.resolve("C.scala"),
      """object X { def f(x: List[T] forSome { type T }): Int = 1 }"""
        .getBytes(StandardCharsets.UTF_8)
    )
    Files.write(
      dir.resolve(".scalafix.conf"),
      """|rules = [DisableSyntax]
         |DisableSyntax.keywords = [forSome]""".stripMargin
        .getBytes(StandardCharsets.UTF_8)
    )

    val baos = new ByteArrayOutputStream()
    Main.run(Array("C.scala"), dir, new PrintStream(baos))
    val out = fansi.Str(new String(baos.toByteArray, StandardCharsets.UTF_8)).plainText
    assert(out.contains("DisableSyntax.forsome"), s"forSome not reported:\n$out")
  }

  test("val is still reported") {
    val dir = Files.createTempDirectory("scalafix-val")
    Files.write(
      dir.resolve("C.scala"),
      """object X { val y = 1 }""".getBytes(StandardCharsets.UTF_8)
    )
    Files.write(
      dir.resolve(".scalafix.conf"),
      """|rules = [DisableSyntax]
         |DisableSyntax.keywords = [val]""".stripMargin
        .getBytes(StandardCharsets.UTF_8)
    )

    val baos = new ByteArrayOutputStream()
    Main.run(Array("C.scala"), dir, new PrintStream(baos))
    val out = fansi.Str(new String(baos.toByteArray, StandardCharsets.UTF_8)).plainText
    assert(out.contains("DisableSyntax.val"), s"val not reported:\n$out")
  }
}

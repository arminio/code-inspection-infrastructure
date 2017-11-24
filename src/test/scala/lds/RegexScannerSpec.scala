package lds

import lds.model.Rule
import lds.scanner.{MatchedResult, RegexScanner}
import org.scalatest.{FreeSpec, Matchers}

class RegexScannerSpec extends FreeSpec with Matchers {

  "scan" - {
    "should look for a regex in a given text" - {
      "and find return the line number matching the regex" in {

        val text =
          """nothing matching here
            |this matches the regex
            |this matches the regex too
            |nothing matching here
            |""".stripMargin
        val tag  = "tag for regex"
        val rule = Rule("^.*(matches).*", tag)

        new RegexScanner(rule).scan(text) should
          contain theSameElementsAs Seq(
          MatchedResult("this matches the regex", lineNumber     = 2, tag),
          MatchedResult("this matches the regex too", lineNumber = 3, tag)
        )
      }

      "and return empty seq if text doesn't have matching lines for the given regex" in {

        val text = "this is a test"
        val rule = Rule("^.*(was).*", "tag")

        new RegexScanner(rule).scan(text) shouldBe Nil
      }
    }
  }
}

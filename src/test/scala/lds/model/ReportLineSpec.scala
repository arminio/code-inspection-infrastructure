package lds.model

import lds.scanner.{MatchedResult, Result}
import org.scalatest.{FreeSpec, Matchers}

class ReportLineSpec extends FreeSpec with Matchers {

  "ReportLine" - {
    "when creating" - {
      "should set the url to the correct line of the file" in {

        val repoUrl        = "http://githib.com/some-special-repo/"
        val branch         = "refs/heads/branchXyz"
        val payloadDetails = PayloadDetails("someRepo", Nil, branch, repoUrl, "commit-123", "")
        val urlToFile      = "/src/main/scala/SomeClass.scala"

        val tag        = "some tag"
        val lineNumber = 95

        val reportLine = ReportLine.build(
          payloadDetails,
          Result(urlToFile, MatchedResult("some matched text in the file", lineNumber, tag)))

        reportLine.urlToSource shouldBe s"$repoUrl/blob/branchXyz$urlToFile#L$lineNumber"
        reportLine.tag         shouldBe tag

      }
    }
  }

}

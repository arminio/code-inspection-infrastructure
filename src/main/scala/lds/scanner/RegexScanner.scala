package lds.scanner

import lds.model.Rule

class RegexScanner(rule: Rule) {

  val compiledRegex = rule.regex.r

  def scan(text: String): Seq[MatchedResult] =
    text.lines.toSeq.zipWithIndex
      .filter {
        case (lineText, _) =>
          lineText match {
            case compiledRegex(_*) => true
            case _                 => false
          }
      }
      .map {
        case (lineText, lineNumber) =>
          MatchedResult(lineText, adjustForBase1Numbering(lineNumber), rule.tag)
      }

  def adjustForBase1Numbering(i: Int): Int = i + 1

}

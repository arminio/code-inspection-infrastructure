package lds.model

import lds.scanner.Result
import play.api.libs.json.Json

case class Report(
  repoName: String,
  repoUrl: String,
  commitId: String,
  authors: Seq[Author],
  inspectionResults: Seq[ReportLine]) {}

object Report {
  implicit val reportLineWrite = Json.writes[ReportLine]
  implicit val reportWrite     = Json.writes[Report]

  def create(payloadDetails: PayloadDetails, results: Seq[Result]) = Report(
    payloadDetails.repositoryName,
    payloadDetails.repositoryUrl,
    payloadDetails.commitId,
    payloadDetails.authors,
    results.map(r => ReportLine.build(payloadDetails, r))
  )
}

case class ReportLine(filePath: String, lineNumber: Int, urlToSource: String, tag: String)
object ReportLine {
  def build(payloadDetails: PayloadDetails, result: Result): ReportLine = {
    val repoUrl: String = payloadDetails.repositoryUrl
    val branch          = payloadDetails.branchRef.diff("refs/heads/")
    new ReportLine(
      result.filePath,
      result.scanResults.lineNumber,
      s"$repoUrl/blob/$branch${result.filePath}#L${result.scanResults.lineNumber}",
      result.scanResults.tag)
  }

}

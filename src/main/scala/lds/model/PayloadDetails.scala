package lds.model

import play.api.libs.json.{Json, OFormat}
import scala.language.implicitConversions

case class Author(name: String, email: String, username: String)

object Author {
  implicit val reads: OFormat[Author] = Json.format[Author]
}

case class PayloadDetails(
  repositoryName: String,
  authors: Seq[Author],
  branchRef: String,
  repositoryUrl: String,
  commitId: String,
  archiveUrl: String
)

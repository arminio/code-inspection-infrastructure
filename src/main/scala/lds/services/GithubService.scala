package lds.services

import cats.implicits._
import com.typesafe.scalalogging.Logger
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

import lds.Errors.{GithubError, InvalidPayload}
import lds.config.LambdaRuntimeProperties
import lds.model.{Author, PayloadDetails}
import play.api.libs.json.{JsLookupResult, Json}

import scala.language.implicitConversions

trait GithubService {
  def checkSignature(payload: String, ghSignature: String): Boolean
  def parsePayloadDetails(paylod: String): Either[GithubError, PayloadDetails]
}

class GithubServiceImpl extends GithubService {

  val logger = Logger(this.getClass)

  def checkSignature(payload: String, ghSignature: String): Boolean = {
    val secret = new SecretKeySpec(LambdaRuntimeProperties.webhookSecretKey.getBytes(), "HmacSHA1")
    val hmac   = Mac.getInstance("HmacSHA1")

    hmac.init(secret)

    val sig           = hmac.doFinal(payload.getBytes("UTF-8"))
    val hashOfPayload = s"sha1=${DatatypeConverter.printHexBinary(sig)}"

    logger.info("hashOfPayload:" + hashOfPayload.toLowerCase())
    logger.info("hashFromGH:" + ghSignature.toLowerCase())

    ghSignature.equalsIgnoreCase(hashOfPayload)
  }

  implicit def str(js: JsLookupResult): String =
    js.as[String]

  def parsePayloadDetails(payload: String): Either[InvalidPayload, PayloadDetails] =
    Either
      .catchNonFatal {
        val jsonPayload = Json.parse(payload)
        val ref         = jsonPayload \ "ref"
        val repoName    = jsonPayload \ "repository" \ "name"
        val repoUrl     = jsonPayload \ "repository" \ "url"
        val commitId    = jsonPayload \ "after"
        val authors     = jsonPayload \ "commits" \\ "author"
        val archiveUrl  = jsonPayload \ "repository" \ "archive_url"

        PayloadDetails(repoName, authors.map(_.as[Author]), ref, repoUrl, commitId, archiveUrl)
      }
      .leftMap { t: Throwable =>
        InvalidPayload(s"Invalid response, details: ${t.getMessage}", t)
      }
}

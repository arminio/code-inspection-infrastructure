package lds.services

import com.typesafe.scalalogging.Logger
import java.io.File

import lds.Errors.{GithubError, ZipProcessingError}
import lds.model.PayloadDetails
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scalaj.http._
import cats.implicits._

class ArtifactService() {
  val logger = Logger("ArtifactManager")

  val savedZipFilePath = s"${FileUtils.getTempDirectoryPath}${File.separatorChar}unzipped"

  def getZipAndExplode(
    githubPersonalAccessToken: String,
    payloadDetails: PayloadDetails,
    branch: String): Either[ZipProcessingError, File] =
    Either
      .catchNonFatal {
        logger.info("starting zip process....")
        getZip(githubPersonalAccessToken, payloadDetails, branch)
        explodeZip()
      }
      .leftMap(t => ZipProcessingError(t.getMessage))

  def getZip(
    githubPersonalAccessToken: String,
    payloadDetails: PayloadDetails,
    branch: String): Unit = {
    val githubZipUri = getArtifactUrl(payloadDetails, branch)
    logger.info(s"Getting code archive from: $githubZipUri")

    downloadFile(githubPersonalAccessToken, githubZipUri, savedZipFilePath, branch)
    logger.info(s"saved archive to: $savedZipFilePath")

  }

  def explodeZip(): File = {
    val explodedZipFile = new File(savedZipFilePath)
    ZipUtil.explode(explodedZipFile)
    logger.info(s"Zip file exploded successfully")
    explodedZipFile
  }

  def downloadFile(githubAccessToken: String, url: String, filename: String, branch: String): Unit =
    retry(5) {
      val resp =
        Http(url)
          .header("Authorization", s"token $githubAccessToken")
          .option(HttpOptions.followRedirects(true))
          .asBytes
      if (resp.isError) {
        val errorMessage = s"Error downloading the zip file from github:\n${new String(resp.body)}"
        logger.error(errorMessage)
        throw new RuntimeException(errorMessage)
      } else {
        logger.info(s"Response code: ${resp.code}")
        logger.debug(s"Got ${resp.body.size} bytes from $url... saving it to $filename")
        val file = new File(filename)
        FileUtils.deleteQuietly(file)
        FileUtils.writeByteArrayToFile(file, resp.body)
        logger.info(s"Saved file: $filename")
      }
    }

  def retry[T](retryCount: Int)(f: => T): T =
    Try(f) match {
      case Success(resp) => resp
      case Failure(t) =>
        logger.warn(s"Got error: ${t.getMessage}, retrying $retryCount more times")
        if (retryCount > 0) {
          Thread.sleep(200)
          retry(retryCount - 1)(f)
        } else throw t
    }

  private def getArtifactUrl(payloadDetails: PayloadDetails, branch: String) =
    payloadDetails.archiveUrl.replace("{archive_format}", "zipball").replace("{/ref}", s"/$branch")
}

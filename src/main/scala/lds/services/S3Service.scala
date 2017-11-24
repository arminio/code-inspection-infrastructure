package lds.services

import java.io.File
import java.nio.charset.StandardCharsets

import awscala.s3.{Bucket, PutObjectResult, S3Client, S3Object}
import cats.implicits._
import lds.Errors._
import lds.config.AwsCredentials
import lds.model.Report
import org.apache.commons.io.{FileUtils, IOUtils}
import play.api.libs.json.Json

class S3Service() {

  lazy val s3Client = new S3Client(AwsCredentials.credentialsProvider)

  val codeInspectionBucketName = "ak-code-inspection"
  //!@  val codeInspectionConfigBucket = "mdtp-code-inspection"

  val codeInspectionBucket = Bucket(codeInspectionBucketName)

  def readConfig(configName: String): Either[S3Error, String] =
    for {
      s3Object      <- getS3Object(configName)
      configContent <- readS3ObjectContent(s3Object)
    } yield configContent

  private def readS3ObjectContent(s3Object: S3Object) =
    Either
      .catchNonFatal(IOUtils.toString(s3Object.content, StandardCharsets.UTF_8))
      .leftMap(S3ErrorReadingFile.apply)

  def getS3Object(fileName: String): Either[S3Error, S3Object] =
    Either.fromOption(
      s3Client.get(codeInspectionBucket, fileName),
      ifNone = S3ObjectNotFound(fileName, codeInspectionBucketName)
    )

  def writeReport(report: Report): PutObjectResult =
    s3Client.put(
      codeInspectionBucket,
      s"reports/${report.repoName}-${report.commitId.substring(0, 7)}-report.txt",
      writeReportToTempFile(report))

  def writeReportToTempFile(report: Report): File = {
    import lds.model.Report._
    val txt        = Json.stringify(Json.toJson(report))
    val reportFile = new File(s"${FileUtils.getTempDirectoryPath}/report.txt")
    FileUtils.write(reportFile, txt, StandardCharsets.UTF_8)
    reportFile
  }

}

package lds

import java.util

import cats.data.EitherT
import cats.implicits._
import com.amazonaws.services.lambda.runtime.Context
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import lds.Errors._
import lds.Utilities.runAndRespond
import lds.config._
import lds.model.{AllRules, Report, Rule}
import lds.scanner.{FileAndDirectoryUtils, RegexMatchingEngine}
import lds.services._
import play.api.libs.json.Json
import pureconfig.syntax._

import scala.beans.BeanProperty
import scala.concurrent.Future

class Handler(
  githubService: GithubService,
  artifactService: ArtifactService,
  lambdaRuntimeProperties: LambdaRuntimeProperties,
  s3Service: S3Service,
  fileAndDirectoryUtils: FileAndDirectoryUtils,
  regexMatchingEngine: RegexMatchingEngine,
) {

  def this() = this(
    new GithubServiceImpl(),
    new ArtifactService,
    LambdaRuntimeProperties,
    new S3Service(),
    new FileAndDirectoryUtils(),
    new RegexMatchingEngine()
  )

  val githubPersonalAccessToken = lambdaRuntimeProperties.githubPersonalAccessToken

  val logger = Logger(classOf[Handler])

  def handle(input: Request, context: Context): Response =
    runAndRespond {
      LogConfigurator.configureLog4jFromSystemProperties()
      for {
        rules    <- getRules(getConfigName(input))
        response <- scanCodeBaseFromGit(input, rules)
      } yield response
    }

  def getRules(configName: String): Either[LdsError, List[Rule]] =
    for {
      config <- s3Service.readConfig(configName)
      rules  <- loadRules(config)
    } yield rules

  def loadRules(config: String) =
    ConfigFactory
      .parseString(config)
      .to[AllRules]
      .map(_.rules)
      .leftMap(ConfigError.apply)

  def getConfigName(input: Request): String = {
    val configName = input.pathParameters.configName
    logger.info(s"Config: $configName")
    configName
  }

  def scanCodeBaseFromGit(input: Request, rules: List[Rule]): Either[GithubError, Response] = {
    val payload = input.body
    logger.info(s"payload:$payload")

    val signature = input.headers.get("X-Hub-Signature").toString

    for {
      _              <- validateSignature(payload, signature)
      payloadDetails <- githubService.parsePayloadDetails(payload)
      explodedZipDir <- artifactService.getZipAndExplode(
                         githubPersonalAccessToken,
                         payloadDetails,
                         payloadDetails.branchRef)
    } yield {
      val results =
        regexMatchingEngine.run(explodedZipDir, rules, new FileAndDirectoryUtils).toList

      val report = Report.create(payloadDetails, results)

      if (results.nonEmpty) {
        s3Service.writeReport(report)
      }

      Response(200, Json.stringify(Json.toJson(report)))
    }

  }

  def validateSignature(
    payload: String,
    signature: String): Either[Errors.InvalidSignature.type, Unit] =
    if (githubService.checkSignature(payload, signature)) {
      logger.info("Signature check passed")
      Right { () }
    } else {
      logger.error("Signature from Github doesn't match what we expect!")
      Left(InvalidSignature)
    }

}

class Request(
  @BeanProperty var body: String,
  @BeanProperty var headers: util.Map[String, Object],
  @BeanProperty var pathParameters: Data) {
  def this() = this("", new util.HashMap(), new Data(""))
}

class Data(@BeanProperty var configName: String) {
  def this() = this("")
}

case class Response(@BeanProperty statusCode: Int, @BeanProperty body: String)

package lds.config

import com.typesafe.scalalogging.Logger

trait LambdaRuntimeProperties {
  def webhookSecretKey: String
  def githubPersonalAccessToken: String
  def region: String
}

object LambdaRuntimeProperties extends LambdaRuntimeProperties {
  def webhookSecretKey          = getEnvValue("WEBHOOK_SECRET_KEY", None)
  def githubPersonalAccessToken = getEnvValue("GITHUB_PERSONAL_ACCESS_TOKEN", None)
  def region                    = getEnvValue("REGION", None, true)

  val logger = Logger(classOf[LambdaRuntimeProperties])

  def getEnvValue(variableName: String, default: Option[String], log: Boolean = false): String = {
    val envValue =
      Option(System.getenv(variableName))
        .getOrElse {
          default.getOrElse {
            val msg = s"FATAL: env variable not found for key '$variableName'"
            logger.error(msg)
            throw new RuntimeException(msg)
          }
        }
    if (log)
      logger.info(s"$variableName: $envValue")

    envValue
  }

}

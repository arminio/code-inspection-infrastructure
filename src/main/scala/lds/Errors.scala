package lds

import pureconfig.error.ConfigReaderFailures

object Errors {

  type ErrorOr[A] = Either[LdsError, A]

  sealed trait LdsError extends Product with Serializable

  // ---

  sealed trait S3Error extends LdsError

  case class S3ObjectNotFound(fileName: String, bucket: String) extends S3Error

  case class S3ErrorReadingFile(throwable: Throwable) extends S3Error

  // ---

  final case class ConfigError(e: ConfigReaderFailures) extends LdsError

  // ---

  sealed trait GithubError extends LdsError
  final case class InvalidPayload(errorMsg: String, throwable: Throwable) extends GithubError
  final case class ZipProcessingError(errorMsg: String) extends GithubError
  final case object InvalidSignature extends GithubError

}

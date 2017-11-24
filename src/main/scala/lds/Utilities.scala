package lds

import com.typesafe.scalalogging.Logger
import lds.Errors.ErrorOr

import scala.util.control.NonFatal

object Utilities {

  val logger = Logger[this.type]

  def runAndRespond(errorOrResponse: ErrorOr[Response]): Response =
    try {
      errorOrResponse match {
        case Right(r)    => r
        case Left(error) => returnError(error.toString)
      }
    } catch {
      case NonFatal(ex) => returnError(ex.getMessage)
    }

  def returnError(errorMsg: String): Response = {
    logger.error(s"An error has occurred: $errorMsg")
    Response(500, errorMsg)
  }

}

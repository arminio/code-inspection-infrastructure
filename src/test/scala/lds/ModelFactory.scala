package lds

import lds.model.{Author, PayloadDetails}
import scala.util.Random

object ModelFactory {

  def aString(s: String = ""): String =
    Random.alphanumeric.take(10) + "_" + s

  def few[T](f: () => T): List[T] =
    List.fill(Random.nextInt(5))(f())

  def anAuthor =
    Author(
      name     = aString("author"),
      email    = aString("email"),
      username = aString("username")
    )

  def aPayloadDetails =
    PayloadDetails(
      repositoryName = aString("repositoryName"),
      authors        = few(() => anAuthor),
      branchRef      = aString("ref"),
      repositoryUrl  = aString("repo"),
      commitId       = aString("commitId"),
      archiveUrl     = aString("archiveUrl")
    )

}

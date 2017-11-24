package lds.model

import lds.Errors.{GithubError, InvalidPayload}
import lds.services.GithubServiceImpl
import org.scalatest.{Matchers, WordSpec}

class GithubServiceSpec extends WordSpec with Matchers {

  "Parsing failing details" should {

    "return an error if not parsable" in {
      val malformedBody = "404 Github has troubles"
      val githubService = new GithubServiceImpl()

      val Left(InvalidPayload(errorMsg, _)) = githubService.parsePayloadDetails(malformedBody)

      errorMsg should startWith("Invalid response, details: ")
    }

    "succeed otherwise" in {
      val expectedPayloadDetails =
        PayloadDetails(
          repositoryName = "repoName",
          authors        = List(Author("John", "john@foo.com", "john4000")),
          branchRef      = "refs/heads/master",
          repositoryUrl  = "https://github.com/foo/bar",
          commitId       = "d002a92",
          archiveUrl     = "https://api.github.com/repos/foo/bar/{archive_format}{/ref}"
        )

      import expectedPayloadDetails._
      val author = authors.head

      val githubResponse =
        s"""
           {
             "ref" : "$branchRef",
             "repository" : {
               "name" : "$repositoryName",
               "url" : "$repositoryUrl",
               "archive_url" : "$archiveUrl"
             },
             "after" : "$commitId",
             "commits" : [
               {
                 "author" : {
                    "name" : "${author.name}",
                    "email" : "${author.email}",
                    "username" : "${author.username}"
                 }
               }
             ]

           }
         """
      val githubService = new GithubServiceImpl()

      val Right(payloadDetails) = githubService.parsePayloadDetails(githubResponse)

      payloadDetails shouldBe expectedPayloadDetails
    }
  }

}

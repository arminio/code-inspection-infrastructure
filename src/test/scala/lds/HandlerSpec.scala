package lds

import java.io.File

import collection.JavaConverters._
import lds.Errors._
import lds.config.LambdaRuntimeProperties
import lds.model.{PayloadDetails, Report, Rule}
import lds.scanner.{FileAndDirectoryUtils, RegexMatchingEngine, Result}
import lds.services.{ArtifactService, GithubService, S3Service}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{EitherValues, FreeSpec, Matchers}
import play.api.libs.json.Json

class HandlerSpec extends FreeSpec with Matchers with MockitoSugar with EitherValues {

  "Getting and parsing rules should" - {
    "fail if S3 failed to return a file " in new Fixtures {
      val error = S3ObjectNotFound("file", "bucket")
      when(mockedS3Service.readConfig(any())).thenReturn(Left(error))

      val res = handler.getRules("a-config-name")

      res shouldBe Left(error)
    }
    "fail if parsing failed" in new Fixtures {
      val malformedConfig = "rules = 1"
      when(mockedS3Service.readConfig(any())).thenReturn(Right(malformedConfig))

      val res = handler.getRules("a-config-name")

      res.left.value shouldBe a[ConfigError]
    }
    "succeed otherwise" in new Fixtures {
      val correctConfig = "rules = []"
      when(mockedS3Service.readConfig(any())).thenReturn(Right(correctConfig))

      val res = handler.getRules("a-config-name")

      res.right.value shouldBe List.empty[Rule]
    }
  }

  "Processing code base from git should" - {

    "fail if request body is not parsable" in new Fixtures {
      val malformedPaylod = "404 - Wrong URL"
      when(mockedGithubService.parsePayloadDetails(any()))
        .thenReturn(Left(InvalidPayload("", new Exception())))
      val request = makeRequest(body = malformedPaylod)

      val res = handler.scanCodeBaseFromGit(request, Nil)

      res.left.value shouldBe a[GithubError]
    }

    "be prevented if GitHub signature is not valid" in new Fixtures {
      when(mockedGithubService.parsePayloadDetails(any()))
        .thenReturn(Right(ModelFactory.aPayloadDetails))
      when(mockedGithubService.checkSignature(any(), any())).thenReturn(false)

      val request = makeRequest()

      val res = handler.scanCodeBaseFromGit(request, List.empty)

      res shouldBe Left(InvalidSignature)
    }

    "fail if downloading and extracting code from a zip archive failed" in new Fixtures {
      when(mockedGithubService.parsePayloadDetails(any()))
        .thenReturn(Right(ModelFactory.aPayloadDetails))
      when(mockedGithubService.checkSignature(any(), any())).thenReturn(true)

      val error = Left(ZipProcessingError("some error message"))
      when(mockedArtifactService.getZipAndExplode(any(), any(), any())).thenReturn(error)

      val request = makeRequest()

      val res = handler.scanCodeBaseFromGit(request, List.empty)

      res shouldBe error
    }

    "return a response incl a report" in new Fixtures {
      val payloadDetails = ModelFactory.aPayloadDetails
      when(mockedGithubService.parsePayloadDetails(any()))
        .thenReturn(Right(payloadDetails))
      when(mockedGithubService.checkSignature(any(), any())).thenReturn(true)
      when(mockedArtifactService.getZipAndExplode(any(), any(), any()))
        .thenReturn(Right(new File("")))
      when(mockedRegexMatchingEngine.run(any(), any(), any())).thenReturn(Nil)

      val request = makeRequest()

      val res = handler.scanCodeBaseFromGit(request, Nil)

      res.right.value shouldBe Response(
        200,
        Json.stringify(
          Json.toJson(
            Report(
              payloadDetails.repositoryName,
              payloadDetails.repositoryUrl,
              payloadDetails.commitId,
              payloadDetails.authors,
              Nil)))
      )

    }
  }

  trait Fixtures {
    val mockedGithubService           = mock[GithubService]
    val mockedArtifactService         = mock[ArtifactService]
    val mockedLambdaRuntimeProperties = mock[LambdaRuntimeProperties]
    val mockedS3Service               = mock[S3Service]
    val mockedFileAndDirectoryUtils   = mock[FileAndDirectoryUtils]
    val mockedRegexMatchingEngine     = mock[RegexMatchingEngine]

    val handler =
      new Handler(
        mockedGithubService,
        mockedArtifactService,
        mockedLambdaRuntimeProperties,
        mockedS3Service,
        mockedFileAndDirectoryUtils,
        mockedRegexMatchingEngine
      )

    def makeRequest(
      body: String                 = "",
      headers: Map[String, Object] = Map("X-Hub-Signature" -> "foo"),
      data: String                 = "") =
      new Request(body, headers.asJava, new Data(data))

  }

}

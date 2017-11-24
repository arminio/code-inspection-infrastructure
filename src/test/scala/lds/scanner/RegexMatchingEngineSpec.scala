package lds.scanner

import ammonite.ops._
import lds.model.Rule
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

class RegexMatchingEngineSpec
    extends FreeSpec
    with MockitoSugar
    with Matchers
    with BeforeAndAfterAll {

  val wd: Path = tmp.dir()

  def createFilesForTest() = {
    write(
      wd / 'zip_file_name_xyz / 'dir1 / "fileA",
      "matching on: secretA\nmatching on: secretA again")
    write(
      wd / 'zip_file_name_xyz / 'dir2 / "fileB",
      "\nmatching on: secretB\nmatching on: secretB again")
    write(
      wd / 'zip_file_name_xyz / 'dir2 / "dir3" / "fileC",
      "matching on: secretC\nmatching on: secretC again")
    write(
      wd / 'zip_file_name_xyz / 'dir2 / "dir3" / "fileD",
      "no match\nto be found in this file\n")
    wd
  }

  override protected def afterAll(): Unit =
    rm ! wd

  "run" - {
    "should scan all the files in all subdirectories and return a report with correct file paths" in {
      val rootDir = createFilesForTest()

      println(rootDir)
      val matches = new RegexMatchingEngine().run(
        explodedZipDir = rootDir.toNIO.toFile,
        rules = Seq(
          Rule(".*secretA.*", "tag 1"),
          Rule(".*secretB.*", "tag 2"),
          Rule(".*secretC.*", "tag 3")
        ),
        fileUtils = new FileAndDirectoryUtils
      )

      matches should have size 6

      matches should contain(
        Result("/dir1/fileA", MatchedResult("matching on: secretA", 1, "tag 1")))
      matches should contain(
        Result("/dir1/fileA", MatchedResult("matching on: secretA again", 2, "tag 1")))

      matches should contain(
        Result("/dir2/fileB", MatchedResult("matching on: secretB", 2, "tag 2")))
      matches should contain(
        Result("/dir2/fileB", MatchedResult("matching on: secretB again", 3, "tag 2")))

      matches should contain(
        Result("/dir2/dir3/fileC", MatchedResult("matching on: secretC", 1, "tag 3")))
      matches should contain(
        Result("/dir2/dir3/fileC", MatchedResult("matching on: secretC again", 2, "tag 3")))
    }

  }

}

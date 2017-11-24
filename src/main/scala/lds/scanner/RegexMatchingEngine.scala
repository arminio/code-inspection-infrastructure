package lds.scanner

import java.io.File
import java.nio.charset.StandardCharsets

import lds.model.Rule
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.{FileFileFilter, TrueFileFilter}

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

case class Result(filePath: String, scanResults: MatchedResult)

class RegexMatchingEngine() {

  def run(
    explodedZipDir: File,
    rules: Seq[Rule],
    fileUtils: FileAndDirectoryUtils): Iterable[Result] = {
    val scanners = rules.map(new RegexScanner(_))

    val filesAndDirs: Iterable[File] = fileUtils.getFiles(explodedZipDir)

    val results = filesAndDirs
      .filterNot(_.isDirectory)
      .par
      .flatMap { file =>
        val fileContent = fileUtils.getFileContents(file)
        scanners.map { scanner =>
          val scanResults: Seq[Result] =
            scanner.scan(fileContent).map(sr => Result(getPath(explodedZipDir, file), sr))
          scanResults
        }

      }
      .flatten
      .seq

    results
  }

  private def getPath(explodedZipDir: File, file: File): String = {
    val pathWithRepoName = file.getAbsolutePath.stripPrefix(explodedZipDir.getAbsolutePath)
    val index            = pathWithRepoName.indexOf('/', 1)
    pathWithRepoName.substring(index)
  }
}

class FileAndDirectoryUtils {

  def getFiles(explodedZipDir: File): Iterable[File] =
    FileUtils
      .listFilesAndDirs(
        explodedZipDir,
        FileFileFilter.FILE,
        TrueFileFilter.INSTANCE
      )
      .asScala

  def getFileContents(file: File) =
    FileUtils.readFileToString(file, StandardCharsets.UTF_8)

}

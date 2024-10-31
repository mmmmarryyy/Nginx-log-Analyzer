package analyzer

import java.io.File
import java.nio.file.{FileSystems, Paths}

object FileUtils {
  private def recursiveListFiles(file: File): Array[File] = {
    val these = file.listFiles
    these.filter(_.isFile) ++ these
      .filter(_.isDirectory)
      .flatMap(recursiveListFiles)
  }

  def getMatchedFile(path: String): Array[File] = {
    val matcher = FileSystems.getDefault().getPathMatcher("glob:" + path)
    recursiveListFiles(new File(".")).filter(c =>
      matcher.matches(Paths.get(c.getPath))
    )
  }

  def generateReportFileName(format: ReportFormat): String = {
    val directory = new File(String.valueOf("reports"))
    if (!directory.exists) {
      directory.mkdir
    }

    val fileType = toFileType(format)
    val numberOfReports = recursiveListFiles(new File("./reports")).length
    if (numberOfReports == 0) "reports/" + s"report.$fileType"
    else s"reports/report_${numberOfReports + 1}.$fileType"
  }
}

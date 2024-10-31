package analyzer

import java.io.File
import org.scalatest.funsuite.AnyFunSuite

class FileUtilsSpec extends AnyFunSuite {

  test("getMatchedFile should return an empty array for an invalid path") {
    val result = FileUtils.getMatchedFile("nonexistent_path")
    assert(result.isEmpty)
  }

  test(
    "getMatchedFile should return an array with the correct files for a valid path"
  ) {
    val result = FileUtils.getMatchedFile(
      "./analyzer/src/test/scala/analyzer/test_logs/*.txt"
    )
    assert(result.length == 2)
    assert(result.map(_.getName).contains("test_logs.txt"))
    assert(result.map(_.getName).contains("test_logs2.txt"))
  }

  test(
    "getMatchedFile should return an array with the correct files for a recursive path"
  ) {
    val result = FileUtils.getMatchedFile("./analyzer/src/test/scala/**/*.txt")
    assert(result.length == 2)
    assert(result.map(_.getName).contains("test_logs.txt"))
    assert(result.map(_.getName).contains("test_logs2.txt"))
  }

  test(
    "generateReportFileName should generate a unique filename for each report"
  ) {
    val filename1 = FileUtils.generateReportFileName(Markdown)
    val filename2 = FileUtils.generateReportFileName(Adoc)
    assert(filename1 !== filename2)
  }
}

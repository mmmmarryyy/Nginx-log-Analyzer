package analyzer

import FileUtils.generateReportFileName

import java.io.File
import java.time.ZonedDateTime
import org.scalatest.funsuite.AnyFunSuite

class LogReportSpec extends AnyFunSuite {

  test(
    "LogReport.createReport should create a correct markdown report for a given LogReport"
  ) {
    val logReport = LogReport(
      "src/test/resources/test_logs/*.txt",
      Some(ZonedDateTime.parse("2024-08-31T00:00:00Z")),
      Some(ZonedDateTime.parse("2024-09-01T00:00:00Z")),
      Some(
        LogStatistics(
          2L,
          Map("192.168.1.1" -> 1L, "127.0.0.1" -> 1L),
          Map("/api/users" -> 1L, "/about.html" -> 1L),
          Map(201 -> 1L, 200 -> 1L),
          Map("POST" -> 1L, "GET" -> 1L),
          1280.0,
          2048.0
        )
      ),
      List[Long](2048, 512)
    )

    val reportFile = new File(generateReportFileName(Markdown))
    LogReport.createReport(logReport, Markdown)
    assert(reportFile.exists)
  }

  test(
    "LogReport.createReport should create a correct adoc report for a given LogReport"
  ) {
    val logReport = LogReport(
      "src/test/resources/test_logs/*.txt",
      Some(ZonedDateTime.parse("2024-08-31T00:00:00Z")),
      Some(ZonedDateTime.parse("2024-09-01T00:00:00Z")),
      Some(
        LogStatistics(
          2L,
          Map("192.168.1.1" -> 1L, "127.0.0.1" -> 1L),
          Map("/api/users" -> 1L, "/about.html" -> 1L),
          Map(201 -> 1L, 200 -> 1L),
          Map("POST" -> 1L, "GET" -> 1L),
          1280.0,
          2048.0
        )
      ),
      List[Long](2048, 512)
    )

    val reportFile = new File(generateReportFileName(Adoc))
    LogReport.createReport(logReport, Adoc)
    assert(reportFile.exists)
  }

  test(
    "LogReport.getStatusName should return the correct status name for a given code"
  ) {
    assert(LogReport.getStatusName(200) === "OK")
    assert(LogReport.getStatusName(304) === "NotModified")
    assert(LogReport.getStatusName(404) === "NotFound")
    assert(LogReport.getStatusName(500) === "InternalServerError")
    assert(LogReport.getStatusName(418) === "Code 418")
  }
}

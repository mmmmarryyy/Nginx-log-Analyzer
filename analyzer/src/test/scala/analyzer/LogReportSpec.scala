package analyzer

import FileUtils.generateReportFileName

import java.time.ZonedDateTime
import org.scalatest.funsuite.AnyFunSuite

import java.io.File

class LogReportSpec extends AnyFunSuite {

  test("LogReport.createReport should create a correct markdown report for a given LogReport") {
    val logReport = LogReport(
      "src/test/resources/test_logs/*.txt",
      Some(ZonedDateTime.parse("2024-08-31T00:00:00Z")),
      Some(ZonedDateTime.parse("2024-09-01T00:00:00Z")),
      Some(2L),
      Some(Map("192.168.1.1" -> 1L, "127.0.0.1" -> 1L)),
      Some(Map("/api/users" -> 1L, "/about.html" -> 1L)),
      Some(Map(201 -> 1L, 200 -> 1L)),
      Some(Map("POST" -> 1L, "GET" -> 1L)),
      Some(1280.0),
      Some(2048.0)
    )

    val reportFile = new File(generateReportFileName("md"))
    LogReport.createReport(logReport, "markdown")
    assert(reportFile.exists)
  }

  test("LogReport.createReport should create a correct adoc report for a given LogReport") {
    val logReport = LogReport(
      "src/test/resources/test_logs/*.txt",
      Some(ZonedDateTime.parse("2024-08-31T00:00:00Z")),
      Some(ZonedDateTime.parse("2024-09-01T00:00:00Z")),
      Some(2L),
      Some(Map("192.168.1.1" -> 1L, "127.0.0.1" -> 1L)),
      Some(Map("/api/users" -> 1L, "/about.html" -> 1L)),
      Some(Map(201 -> 1L, 200 -> 1L)),
      Some(Map("POST" -> 1L, "GET" -> 1L)),
      Some(1280.0),
      Some(2048.0)
    )

    val reportFile = new File(generateReportFileName("adoc"))
    LogReport.createReport(logReport, "adoc")
    assert(reportFile.exists)
  }

  test("LogReport.getStatusName should return the correct status name for a given code") {
    assert(LogReport.getStatusName(200) === "OK")
    assert(LogReport.getStatusName(304) === "NotModified")
    assert(LogReport.getStatusName(404) === "NotFound")
    assert(LogReport.getStatusName(500) === "InternalServerError")
    assert(LogReport.getStatusName(418) === "Code 418")
  }
}
package analyzer

import java.time.ZonedDateTime
import org.scalatest.funsuite.AnyFunSuite

class LogAnalyzerSpec extends AnyFunSuite {
  test(
    "LogAnalyzer.analyzeLogRecords should return a correct LogReport for empty records"
  ) {
    val logRecords = Iterator.empty[LogRecord]
    val result = LogAnalyzer.analyzeLogRecords(
      logRecords,
      LogReport("src/*.txt", None, None, None, List[Long]())
    )
    assert(
      result === LogReport(
        "src/*.txt",
        None,
        None,
        None,
        List[Long]()
      )
    )
  }

  test(
    "LogAnalyzer.analyzeLogRecords should return a correct LogReport for a single record"
  ) {
    val logRecords = Iterator(
      LogRecord(
        "127.0.0.1",
        "-",
        ZonedDateTime.parse("2024-08-31T12:34:56Z"),
        "\"GET /index.html HTTP/1.1\"",
        "GET",
        "/index.html",
        200,
        1024,
        "https://www.example.com/",
        "Mozilla/5.0"
      )
    )
    val result = LogAnalyzer.analyzeLogRecords(
      logRecords,
      LogReport("somePath", None, None, None, List[Long]())
    )

    assert(
      result === LogReport(
        "somePath",
        None,
        None,
        Some(
          LogStatistics(
            1L,
            Map("127.0.0.1" -> 1L),
            Map("/index.html" -> 1L),
            Map(200 -> 1L),
            Map("GET" -> 1L),
            1024.0,
            1024.0
          )
        ),
        List[Long](1024)
      )
    )
  }

  test(
    "LogAnalyzer.analyzeLogRecords should return a correct LogReport for multiple records"
  ) {
    val logRecords = Iterator(
      LogRecord(
        "127.0.0.1",
        "-",
        ZonedDateTime.parse("2024-08-31T12:34:56Z"),
        "\"GET /index.html HTTP/1.1\"",
        "GET",
        "/index.html",
        200,
        1024,
        "https://www.example.com/",
        "Mozilla/5.0"
      ),
      LogRecord(
        "192.168.1.1",
        "-",
        ZonedDateTime.parse("2024-08-31T12:35:00Z"),
        "\"POST /api/users HTTP/1.1\"",
        "POST",
        "/api/users",
        201,
        2048,
        "https://www.example.com/",
        "Chrome/80"
      ),
      LogRecord(
        "127.0.0.1",
        "-",
        ZonedDateTime.parse("2024-08-31T12:35:10Z"),
        "\"GET /about.html HTTP/1.1\"",
        "GET",
        "/about.html",
        200,
        512,
        "https://www.example.com/",
        "Firefox/70"
      )
    )
    val result = LogAnalyzer.analyzeLogRecords(
      logRecords,
      LogReport("somePath", None, None, None, List[Long]())
    )
    assert(
      result === LogReport(
        "somePath",
        None,
        None,
        Some(
          LogStatistics(
            3L,
            Map("127.0.0.1" -> 2L, "192.168.1.1" -> 1L),
            Map("/index.html" -> 1L, "/api/users" -> 1L, "/about.html" -> 1L),
            Map(200 -> 2L, 201 -> 1L),
            Map("GET" -> 2L, "POST" -> 1L),
            1194.6666666666667,
            2048.0
          )
        ),
        List[Long](1024, 2048, 512)
      )
    )
  }
}

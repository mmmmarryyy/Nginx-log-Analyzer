package analyzer

import analyzer.FileUtils.getMatchedFile
import analyzer.LogParser.{openURLSource, openFileSource}

import java.time.ZonedDateTime
import cats.effect.IO
import munit.CatsEffectSuite

class LogParserSpec extends CatsEffectSuite {
  test("LogParser.parseLogRecord should parse a valid log record correctly") {
    val logRecordStr =
      "127.0.0.1 - - [21/Jul/2024:12:34:56 +0000] \"GET /index.html HTTP/1.1\" 200 1024 \"https://www.example.com/\" \"Mozilla/5.0\""
    val result = LogParser.parseLogRecord(logRecordStr)

    IO(
      assert(
        result == LogRecord(
          "127.0.0.1",
          "-",
          ZonedDateTime
            .parse("21/Jul/2024:12:34:56 +0000", LogParser.formatter),
          "\"GET /index.html HTTP/1.1\"",
          "GET",
          "/index.html",
          200,
          1024,
          "https://www.example.com/",
          "Mozilla/5.0"
        )
      )
    )
  }

  test(
    "LogParser.parseLogRecord should parse a log record with IPv6 address correctly"
  ) {
    val logRecordStr =
      "2001:0db8:85a3:0000:0000:8a2e:0370:7334 - - [21/Jul/2024:12:34:56 +0000] \"GET /index.html HTTP/1.1\" 200 1024 \"https://www.example.com/\" \"Mozilla/5.0\""
    val result = LogParser.parseLogRecord(logRecordStr)
    IO(
      assert(
        result == LogRecord(
          "2001:0db8:85a3:0000:0000:8a2e:0370:7334",
          "-",
          ZonedDateTime
            .parse("21/Jul/2024:12:34:56 +0000", LogParser.formatter),
          "\"GET /index.html HTTP/1.1\"",
          "GET",
          "/index.html",
          200,
          1024,
          "https://www.example.com/",
          "Mozilla/5.0"
        )
      )
    )
  }

  test(
    "LogParser.parseLogRecord should parse a log record with a remote user correctly"
  ) {
    val logRecordStr =
      "127.0.0.1 - user123 [21/Jul/2024:12:34:56 +0000] \"GET /index.html HTTP/1.1\" 200 1024 \"https://www.example.com/\" \"Mozilla/5.0\""
    val result = LogParser.parseLogRecord(logRecordStr)
    IO(
      assert(
        result == LogRecord(
          "127.0.0.1",
          "user123",
          ZonedDateTime
            .parse("21/Jul/2024:12:34:56 +0000", LogParser.formatter),
          "\"GET /index.html HTTP/1.1\"",
          "GET",
          "/index.html",
          200,
          1024,
          "https://www.example.com/",
          "Mozilla/5.0"
        )
      )
    )
  }

  test(
    "LogParser.parseLogRecord should throw an exception for an invalid log record"
  ) {
    val logRecordStr =
      "Invalid log record"
    val thrown = intercept[IllegalArgumentException] {
      LogParser.parseLogRecord(logRecordStr)
    }
    IO(assert(thrown.getMessage.contains("Invalid log record")))
  }

  test("LogParser.parseTimeLocal should parse a valid time string correctly") {
    val timeStr = "21/Jul/2024:12:34:56 +0000"
    val result = LogParser.parseTimeLocal(timeStr)
    IO(assert(result == ZonedDateTime.parse(timeStr, LogParser.formatter)))
  }

  test(
    "LogParser.parseTimeLocal should throw an exception for an invalid time string"
  ) {
    val timeStr = "21/Jul/2024:12:34:56"
    val thrown = intercept[IllegalArgumentException] {
      LogParser.parseTimeLocal(timeStr)
    }
    IO(assert(thrown.getMessage.contains("Invalid date-time format")))
  }

  test("LogParser.getLogRecords get correctly from URI") {
    openURLSource(
      "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs"
    ).bracket { source =>
      for {
        result <- LogParser.getLogRecords(source, None, None, None, None)
        _ <- IO(assert(result.size == 51462))
      } yield ()
    } { source =>
      IO(source.close())
    }
  }

  test("LogParser.getLogRecords get correctly from local file") {
    openFileSource(
      getMatchedFile(
        "./analyzer/src/test/scala/analyzer/test_logs/test_logs.txt"
      ).head
    ).bracket { source =>
      for {
        result <- LogParser.getLogRecords(source, None, None, None, None)
        _ <- IO(assert(result.size == 4))
      } yield ()
    } { source =>
      IO(source.close())
    }
  }

  test("LogParser.getLogRecords works correctly with from option") {
    openFileSource(
      getMatchedFile(
        "./analyzer/src/test/scala/analyzer/test_logs/test_logs.txt"
      ).head
    ).bracket { source =>
      for {
        result <- LogParser.getLogRecords(
          source,
          Some(InputUtils.parseDate("2024-09-01")),
          None,
          None,
          None
        )
        _ <- IO(assert(result.size == 3))
      } yield ()
    } { source =>
      IO(source.close())
    }
  }

  test("LogParser.getLogRecords works correctly with to option") {
    openFileSource(
      getMatchedFile(
        "./analyzer/src/test/scala/analyzer/test_logs/test_logs.txt"
      ).head
    ).bracket { source =>
      for {
        result <- LogParser.getLogRecords(
          source,
          None,
          Some(InputUtils.parseDate("2024-11-03")),
          None,
          None
        )
        _ <- IO(assert(result.size == 3))
      } yield ()
    } { source =>
      IO(source.close())
    }
  }

  test("LogParser.getLogRecords works correctly with from and to options") {
    openFileSource(
      getMatchedFile(
        "./analyzer/src/test/scala/analyzer/test_logs/test_logs.txt"
      ).head
    ).bracket { source =>
      for {
        result <- LogParser.getLogRecords(
          source,
          Some(InputUtils.parseDate("2024-09-01")),
          Some(InputUtils.parseDate("2024-11-03")),
          None,
          None
        )
        _ <- IO(assert(result.size == 2))
      } yield ()
    } { source =>
      IO(source.close())
    }
  }

  test("getLogRecordsFromLocal should filter records by filter field") {
    val filterField = Some(Resource)
    val filterValue = Some("/index.html")
    openFileSource(
      getMatchedFile(
        "./analyzer/src/test/scala/analyzer/test_logs/test_logs.txt"
      ).head
    ).bracket { source =>
      for {
        result <- LogParser.getLogRecords(
          source,
          None,
          None,
          filterField,
          filterValue
        )
        _ <- IO(assert(result.size == 2))
      } yield ()
    } { source =>
      IO(source.close())
    }
  }
}

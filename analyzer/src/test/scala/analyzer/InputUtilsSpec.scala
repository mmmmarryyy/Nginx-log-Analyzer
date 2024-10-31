package analyzer

import java.time.ZonedDateTime
import org.scalatest.funsuite.AnyFunSuite

class InputUtilsSpec extends AnyFunSuite {

  test("InputUtils.parseArgs should parse valid arguments correctly") {
    val args = Array(
      "--path",
      "src/test/resources/test_logs/*.txt",
      "--from",
      "2024-08-31",
      "--to",
      "2024-09-01",
      "--format",
      "adoc"
    )
    val result = InputUtils.parseArgs(args)
    assert(
      result === Some(
        AnalyzerArgs(
          "src/test/resources/test_logs/*.txt",
          Some(ZonedDateTime.parse("2024-08-31T00:00:00Z")),
          Some(ZonedDateTime.parse("2024-09-01T00:00:00Z")),
          Adoc,
          None,
          None
        )
      )
    )
  }

  test(
    "InputUtils.parseArgs should parse arguments without to date correctly"
  ) {
    val args = Array(
      "--path",
      "src/test/resources/test_logs/*.txt",
      "--from",
      "2024-08-31"
    )
    val result = InputUtils.parseArgs(args)
    assert(
      result === Some(
        AnalyzerArgs(
          "src/test/resources/test_logs/*.txt",
          Some(ZonedDateTime.parse("2024-08-31T00:00:00Z")),
          None,
          Markdown,
          None,
          None
        )
      )
    )
  }

  test(
    "InputUtils.parseArgs should parse arguments without from and to dates correctly"
  ) {
    val args = Array("--path", "src/test/resources/test_logs/*.txt")
    val result = InputUtils.parseArgs(args)
    assert(
      result === Some(
        AnalyzerArgs(
          "src/test/resources/test_logs/*.txt",
          None,
          None,
          Markdown,
          None,
          None
        )
      )
    )
  }

  test("InputUtils.parseArgs should parse arguments with a format option") {
    val args =
      Array("--path", "src/test/resources/test_logs/*.txt", "--format", "adoc")
    val result = InputUtils.parseArgs(args)
    assert(
      result === Some(
        AnalyzerArgs(
          "src/test/resources/test_logs/*.txt",
          None,
          None,
          Adoc,
          None,
          None
        )
      )
    )
  }

  test("InputUtils.parseArgs should return None for invalid arguments") {
    val args = Array("--path")
    val result = InputUtils.parseArgs(args)
    assert(result.isEmpty)
  }

  test("InputUtils.parseDate should parse date string correctly") {
    val dateStr = "2024-08-31"
    val result = InputUtils.parseDate(dateStr)
    assert(result === ZonedDateTime.parse("2024-08-31T00:00:00Z"))
  }

  test("InputUtils.parseDate should parse date-time string correctly") {
    val dateStr = "2011-12-03T10:15:30+01:00"
    val result = InputUtils.parseDate(dateStr)
    assert(result === ZonedDateTime.parse(dateStr))
  }

  test(
    "InputUtils.parseArgs should parse arguments with filter field and value"
  ) {
    val args = Array(
      "--path",
      "src/test/resources/test_logs/*.txt",
      "--filter-field",
      "agent",
      "--filter-value",
      "Mozilla"
    )
    val result = InputUtils.parseArgs(args)
    assert(
      result === Some(
        AnalyzerArgs(
          "src/test/resources/test_logs/*.txt",
          None,
          None,
          Markdown,
          Some(HttpUserAgent),
          Some("Mozilla")
        )
      )
    )
  }
}

// InputUtils.scala

package analyzer

import java.time.{LocalDate, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

object InputUtils {
  def parseArgs(
      args: Seq[String]
  ): Option[AnalyzerArgs] = {
    val parser = new scopt.OptionParser[AnalyzerArgs]("analyzer") {
      opt[String]("path")
        .required()
        .text("Path to log files (local path or URL)")
        .action((x, c) => c.copy(path = x))
      opt[String]("from")
        .text("Start date in ISO 8601 format (YYYY-MM-DD'T'HH:mm:ss)")
        .action((x, c) => c.copy(from = Some(parseDate(x))))
      opt[String]("to")
        .text("End date in ISO 8601 format (YYYY-MM-DD'T'HH:mm:ss)")
        .action((x, c) => c.copy(to = Some(parseDate(x))))
      opt[String]("format")
        .text("Output format (markdown or adoc). Default: markdown")
        .optional()
        .action((x, c) => c.copy(format = parseFormatString(x)))
      opt[String]("filter-field")
        .text("Field to filter by (e.g., 'agent', 'method')")
        .optional()
        .action((x, c) => c.copy(filterField = parseFilterFieldString(x)))
      opt[String]("filter-value")
        .text("Value to filter by (e.g., 'Mozilla*', 'GET')")
        .optional()
        .action((x, c) => c.copy(filterValue = Some(x)))
    }

    parser.parse(args, AnalyzerArgs()) match {
      case Some(config) =>
        Some(
          AnalyzerArgs(
            config.path,
            config.from,
            config.to,
            config.format,
            config.filterField,
            config.filterValue
          )
        )
      case _ => None
    }
  }

  def parseDate(dateStr: String): ZonedDateTime = {
    try {
      ZonedDateTime.parse(dateStr)
    } catch {
      case _: Exception =>
        val localDate =
          LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
        localDate.atStartOfDay(ZoneOffset.UTC)
    }
  }
}

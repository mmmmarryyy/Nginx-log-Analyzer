package analyzer

import java.time.{LocalDate, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

object InputUtils {
  private case class Config(
      path: String = "",
      from: Option[ZonedDateTime] = None,
      to: Option[ZonedDateTime] = None,
      format: String = "markdown"
  )

  def parseArgs(
      args: Seq[String]
  ): Option[(String, Option[ZonedDateTime], Option[ZonedDateTime], String)] = {
    val parser = new scopt.OptionParser[Config]("analyzer") {
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
        .action((x, c) => c.copy(format = x))
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        Some(config.path, config.from, config.to, config.format)
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

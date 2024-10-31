package analyzer

import java.time.ZonedDateTime

case class AnalyzerArgs(
    path: String = "",
    from: Option[ZonedDateTime] = None,
    to: Option[ZonedDateTime] = None,
    format: ReportFormat = Markdown,
    filterField: Option[FilterField] = None,
    filterValue: Option[String] = None
)

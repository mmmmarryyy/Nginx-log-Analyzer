package analyzer

sealed trait ReportFormat
case object Markdown extends ReportFormat
case object Adoc extends ReportFormat

def parseFormatString(formatStr: String): ReportFormat =
  formatStr.toLowerCase match
    case "markdown" => Markdown
    case "adoc"     => Adoc
    case _          => Markdown

def toFileType(format: ReportFormat): String =
  format match
    case Markdown => "md"
    case Adoc     => "adoc"

package analyzer

import cats.effect.IO

import java.io.File
import java.net.URL
import java.time.ZonedDateTime
import java.time.format.{DateTimeFormatterBuilder, DateTimeParseException}
import java.util.Locale
import scala.io.Source
import scala.util.matching.Regex

object LogParser {
  private val remoteAddrStr = "remoteAddr"
  private val remoteUserStr = "remoteUser"
  private val timeLocalStr = "timeLocal"
  private val requestStr = "request"
  private val httpMethodStr = "httpMethod"
  private val resourceStr = "resource"
  private val statusStr = "status"
  private val bodyBytesSentStr = "bodyBytesSent"
  private val httpRefererStr = "httpReferer"
  private val userAgentStr = "userAgent"

  private val ipv4Pattern =
    "(?:^|\\b(?<!\\.))(?:1?\\d\\d?|2[0-4]\\d|25[0-5])(?:\\.(?:1?\\d\\d?|2[0-4]\\d|25[0-5])){3}(?=$|[^\\w.])"
  private val ipv6Pattern1 = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}"
  private val ipv6Pattern2 =
    "((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)"
  private val remoteUserPattern = "-|[a-z_][a-z0-9_]{0,30}"
  private val timeLocalPattern =
    """(?<date>[0-3][0-9]\/\w{3}\/[12]\d{3}):(?<time>\d\d:\d\d:\d\d).*"""
  private val resourcePattern = """(\/[^\s]*)"""
  private val requestPattern =
    s"""\"(?<$httpMethodStr>(GET|POST|HEAD|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH))""" + """\s""" + s"""(?<$resourceStr>($resourcePattern))""" + """\s(HTTP/\d\.\d)\""""

  private val logPattern = new Regex(
    s"""(?<$remoteAddrStr>(($ipv4Pattern)|($ipv6Pattern1)|($ipv6Pattern2)))""" + """\s-\s""" +
      s"""(?<$remoteUserStr>($remoteUserPattern))""" + """\s\[""" +
      s"""(?<$timeLocalStr>($timeLocalPattern))""" + """\]\s""" +
      s"""(?<$requestStr>($requestPattern))""" + """\s""" +
      s"""(?<$statusStr>""" + """\d{3})\s""" +
      s"""(?<$bodyBytesSentStr>""" + """\d+)\s\"""" +
      s"""(?<$httpRefererStr>""" + """[^\s]+)\"\s\"""" +
      s"""(?<$userAgentStr>""" + """[^\"]+)\""""
  )

  val formatter = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
    .toFormatter()
    .withLocale(Locale.US)

  def openFileSource(file: File): IO[Source] = IO(Source.fromFile(file))

  def openURLSource(link: String): IO[Source] = IO(
    Source.fromURL(new URL(link))
  )

  def getLogRecords(
      source: Source,
      from: Option[ZonedDateTime],
      to: Option[ZonedDateTime],
      filterField: Option[FilterField],
      filterValue: Option[String]
  ): IO[Iterator[LogRecord]] = IO {
    source
      .getLines()
      .map(LogParser.parseLogRecord)
      .filter(line => {
        from.forall(_.isBefore(line.timeLocal)) && to.forall(
          _.isAfter(line.timeLocal)
        ) && applyFilter(line, filterField, filterValue)
      })
  }

  def parseLogRecord(line: String): LogRecord = {
    logPattern.findFirstMatchIn(line) match {
      case Some(m) =>
        LogRecord(
          m.group(remoteAddrStr),
          m.group(remoteUserStr),
          parseTimeLocal(m.group(timeLocalStr)),
          m.group(requestStr),
          m.group(httpMethodStr),
          m.group(resourceStr),
          m.group(statusStr).toInt,
          m.group(bodyBytesSentStr).toInt,
          m.group(httpRefererStr),
          m.group(userAgentStr)
        )
      case None =>
        throw new IllegalArgumentException(s"Invalid log record: $line")
    }
  }

  def parseTimeLocal(dateStr: String): ZonedDateTime = {
    try {
      ZonedDateTime.parse(dateStr, formatter)
    } catch {
      case e: DateTimeParseException =>
        throw new IllegalArgumentException(
          s"Invalid date-time format: $dateStr",
          e
        )
    }
  }

  private def applyFilter(
      record: LogRecord,
      filterField: Option[FilterField],
      filterValue: Option[String]
  ): Boolean = {
    (filterField, filterValue) match {
      case (Some(field), Some(value)) =>
        field match {
          case RemoteAddress => record.remoteAddress == value
          case RemoteUser    => record.remoteUser == value
          case HttpMethod    => record.httpMethod == value
          case Resource      => record.resource == value
          case Status        => record.status.toString == value
          case BodyBytesSent => record.bodyBytesSent.toString == value
          case HttpReferer   => record.httpReferer == value
          case HttpUserAgent => record.httpUserAgent == value
        }
      case _ => true
    }
  }
}

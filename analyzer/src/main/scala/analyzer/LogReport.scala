package analyzer

import FileUtils.generateReportFileName

import java.io.{File, FileWriter}
import java.time.ZonedDateTime
import scala.util.Try

case class LogReport(
    filePath: String,
    startDate: Option[ZonedDateTime],
    endDate: Option[ZonedDateTime],
    logStatistics: Option[LogStatistics],
    responseSizes: List[Long]
)

object LogReport {
  private val generalInformationStr = "General information"
  private val metricsStr = "Metrics"
  private val fieldStr = "Field"
  private val valueStr = "Value"
  private val pathStr = "Path"
  private val startDateStr = "Start date"
  private val endDateStr = "End date"
  private val numberOfRequestsStr = "Number of requests"
  private val averageResponseSizeStr = "Average response size"
  private val p95responseSizeStr = "95p response size"
  private val requestedResourcesStr = "Requested resources"
  private val resourcesStr = "Resource"
  private val quantityStr = "Quantity"
  private val responseCodesStr = "Response codes"
  private val codeStr = "Code"
  private val statusNameStr = "Status name"
  private val methodsStr = "Methods Statistics"
  private val methodStr = "Method"
  private val addressesStr = "Addresses Statistics"
  private val addressStr = "Remote Address"

  def createReport(logReport: LogReport, format: ReportFormat): Unit = {
    val fileWriter = new FileWriter(new File(generateReportFileName(format)))
    val reportContent = generateReportContent(logReport, format)
    fileWriter.write(reportContent)
    fileWriter.close()
  }

  def getStatusName(code: Int): String = {
    Try(HttpCode(code).toString).getOrElse(s"Code $code")
  }

  private object HttpCode extends Enumeration {
    val OK = Value(200)
    val NotModified = Value(304)
    val NotFound = Value(404)
    val InternalServerError = Value(500)
  }

  private def generateReportContent(
      logReport: LogReport,
      format: ReportFormat
  ): String = {
    def addSection(
        title: String,
        head: (String, String),
        data: Seq[(String, String)]
    ): String = {
      val sectionTitle = format match {
        case Markdown => s"#### $title\n"
        case Adoc     => s"== $title ==\n"
      }
      val sectionData = if (data.isEmpty) {
        format match {
          case Markdown => "| - | - |\n"
          case Adoc     => "| - | - |\n"
        }
      } else {
        val header = format match {
          case Markdown => s"${head._1} | ${head._2}\n|:---|---:\n"
          case Adoc     => s"${head._1} | ${head._2}\n|===\n"
        }
        header + data.map(formatRow).mkString + (format match {
          case Markdown => ""
          case Adoc     => "|===\n"
        })
      }
      sectionTitle + sectionData + "\n"
    }

    def formatRow(row: (String, String)): String = {
      format match {
        case Markdown => s"| ${row._1} | ${row._2} |\n"
        case Adoc     => s"| ${row._1} |\n${row._2}\n\n"
      }
    }

    List(
      addSection(
        generalInformationStr,
        (fieldStr, valueStr),
        Seq(
          (pathStr, logReport.filePath),
          (startDateStr, logReport.startDate.getOrElse("-").toString),
          (endDateStr, logReport.endDate.getOrElse("-").toString)
        )
      ),
      logReport.logStatistics match {
        case Some(stats) =>
          List(
            addSection(
              metricsStr,
              (metricsStr, valueStr),
              Seq(
                (numberOfRequestsStr, stats.totalRequests.toString),
                (averageResponseSizeStr, s"${stats.avgResponseBodySize}b"),
                (p95responseSizeStr, s"${stats.p95ResponseBodySize}b")
              )
            ),
            addSection(
              requestedResourcesStr,
              (resourcesStr, quantityStr),
              stats.topResources.map { case (k, v) => (k, v.toString) }.toSeq
            ),
            addSection(
              responseCodesStr,
              (codeStr, statusNameStr),
              stats.topStatusCodes.map { case (k, v) =>
                (k.toString, getStatusName(k))
              }.toSeq
            ),
            addSection(
              methodsStr,
              (methodStr, quantityStr),
              stats.topMethods.map { case (k, v) => (k, v.toString) }.toSeq
            ),
            addSection(
              addressesStr,
              (addressStr, quantityStr),
              stats.topRemoteAddresses.map { case (k, v) =>
                (k, v.toString)
              }.toSeq
            )
          ).mkString
        case None =>
          List(
            addSection(
              metricsStr,
              (metricsStr, valueStr),
              Seq(
                (numberOfRequestsStr, "-"),
                (averageResponseSizeStr, "-"),
                (p95responseSizeStr, "-")
              )
            ),
            addSection(
              requestedResourcesStr,
              (resourcesStr, quantityStr),
              Seq.empty
            ),
            addSection(responseCodesStr, (codeStr, statusNameStr), Seq.empty),
            addSection(methodsStr, (methodStr, quantityStr), Seq.empty),
            addSection(addressesStr, (addressStr, quantityStr), Seq.empty)
          ).mkString
      }
    ).mkString
  }
}

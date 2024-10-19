package analyzer

import FileUtils.generateReportFileName

import java.io.{File, FileWriter}
import java.time.ZonedDateTime
import scala.util.Try

case class LogReport(
    filePath: String,
    startDate: Option[ZonedDateTime],
    endDate: Option[ZonedDateTime],
    totalRequests: Option[Long],
    topRemoteAddresses: Option[Map[String, Long]],
    topResources: Option[Map[String, Long]],
    topStatusCodes: Option[Map[Int, Long]],
    topMethods: Option[Map[String, Long]],
    avgResponseBodySize: Option[Double],
    p95ResponseBodySize: Option[Double]
)

object LogReport {
  private val generalInformationStr = "General information"
  private val metricsStr = "Metrics"
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

  def createReport(logReport: LogReport, format: String): Unit = {
    if (format == "adoc") {
      createAdocReport(logReport)
    } else {
      createMarkdownReport(logReport)
    }
  }

  private def createMarkdownReport(logReport: LogReport): Unit = {
    val fileWriter = new FileWriter(new File(generateReportFileName("md")))
    fileWriter.write(
      s"#### $generalInformationStr\n"
    )
    fileWriter.write(s"| $metricsStr | $valueStr |\n")
    fileWriter.write("|:--------:|---------:|\n")
    fileWriter.write(
      s"|$pathStr|${logReport.filePath}|\n"
    )
    fileWriter.write(
      s"|$startDateStr|${logReport.startDate.getOrElse("-")} |\n"
    )
    fileWriter.write(
      s"|$endDateStr|${logReport.endDate.getOrElse("-")}|\n"
    )
    fileWriter.write(
      s"|$numberOfRequestsStr|${logReport.totalRequests.getOrElse("-")}|\n"
    )
    fileWriter.write(
      s"|$averageResponseSizeStr|${logReport.avgResponseBodySize.getOrElse("-")}b|\n"
    )
    fileWriter.write(
      s"|$p95responseSizeStr|${logReport.p95ResponseBodySize.getOrElse("-")}b|\n"
    )

    fileWriter.write(s"\n#### $requestedResourcesStr\n")
    fileWriter.write(s"| $resourcesStr | $quantityStr |\n")
    fileWriter.write("|:-------:|-----------:|\n")
    logReport.topResources match {
      case Some(topResources) =>
        topResources.foreach { case (resource, count) =>
          fileWriter.write(
            s"| $resource | $count |\n"
          )
        }
      case None => fileWriter.write(s"| - | - | - |\n")
    }

    fileWriter.write(s"\n#### $responseCodesStr\n")
    fileWriter.write(s"| $quantityStr | $statusNameStr | $quantityStr |\n")
    fileWriter.write("|:---:|:---:|-----------:|\n")
    logReport.topStatusCodes match {
      case Some(topStatusCodes) =>
        topStatusCodes.foreach { case (code, count) =>
          fileWriter.write(s"| $code | ${getStatusName(code)} | $count |\n")
        }
      case None => fileWriter.write(s"| - | - | - |\n")
    }

    fileWriter.write(s"\n#### $methodsStr\n")
    fileWriter.write(s"| $methodStr | $quantityStr |\n")
    fileWriter.write("|:-------:|-----------:|\n")
    logReport.topMethods match {
      case Some(topMethods) =>
        topMethods.foreach { case (method, count) =>
          fileWriter.write(
            s"| $method | $count |\n"
          )
        }
      case None => fileWriter.write(s"| - | - | - |\n")
    }

    fileWriter.write(s"\n#### $addressesStr\n")
    fileWriter.write(s"| $addressStr | $quantityStr |\n")
    fileWriter.write("|:-------:|-----------:|\n")
    logReport.topRemoteAddresses match {
      case Some(topRemoteAddresses) =>
        topRemoteAddresses.foreach { case (address, count) =>
          fileWriter.write(
            s"| $address | $count |\n"
          )
        }
      case None => fileWriter.write(s"| - | - | - |\n")
    }
    fileWriter.close()
  }

  private def createAdocReport(logReport: LogReport): Unit = {
    val fileWriter = new FileWriter(new File(generateReportFileName("adoc")))
    fileWriter.write(s"== $generalInformationStr ==\n[cols=\"1,1\"]\n|===\n")
    fileWriter.write(s"|$metricsStr|$valueStr\n\n")
    fileWriter.write(
      s"|$pathStr\n|${logReport.filePath}\n\n"
    )
    fileWriter.write(
      s"|$startDateStr\n|${logReport.startDate.getOrElse("-")}\n\n"
    )
    fileWriter.write(
      s"|$endDateStr\n|${logReport.endDate.getOrElse("-")}\n\n"
    )
    fileWriter.write(
      s"|$numberOfRequestsStr\n|${logReport.totalRequests.getOrElse("-")}\n\n"
    )
    fileWriter.write(
      s"|$averageResponseSizeStr\n|${logReport.avgResponseBodySize.getOrElse("-")}b\n\n"
    )
    fileWriter.write(
      s"|$p95responseSizeStr\n|${logReport.p95ResponseBodySize.getOrElse("-")}b\n\n"
    )

    fileWriter.write(
      s"|===\n\n== $requestedResourcesStr ==\n[cols=\"1,1\"]\n|===\n"
    )
    fileWriter.write(s"|$resourcesStr|$quantityStr\n\n")
    logReport.topResources match {
      case Some(topResources) =>
        topResources.foreach { case (resource, count) =>
          fileWriter.write(s"|$resource|\n$count\n\n")
        }
      case None => fileWriter.write(s"| - | - | - |\n")
    }

    fileWriter.write(
      s"|===\n\n== $responseCodesStr ==\n[cols=\"1,1,1\"]\n|===\n"
    )
    fileWriter.write(s"|$quantityStr|$statusNameStr|$quantityStr\n\n")
    logReport.topStatusCodes match {
      case Some(topStatusCodes) =>
        topStatusCodes.foreach { case (code, count) =>
          fileWriter.write(s"|$code|\n${getStatusName(code)}|\n$count\n\n")
        }
      case None => fileWriter.write(s"| - | - | - |\n")
    }

    fileWriter.write(s"|===\n\n== $methodsStr ==\n[cols=\"1,1\"]\n|===\n")
    fileWriter.write(s"|$methodStr|$quantityStr\n\n")
    logReport.topMethods match {
      case Some(topMethods) =>
        topMethods.foreach { case (method, count) =>
          fileWriter.write(s"|$method|\n$count\n\n")
        }
      case None => fileWriter.write(s"| - | - | - |\n")
    }

    fileWriter.write(s"|===\n\n== $addressesStr ==\n[cols=\"1,1\"]\n|===\n")
    fileWriter.write(s"|$addressStr|$quantityStr\n\n")
    logReport.topRemoteAddresses match {
      case Some(topRemoteAddresses) =>
        topRemoteAddresses.foreach { case (address, count) =>
          fileWriter.write(s"|$address|\n$count\n\n")
        }
      case None => fileWriter.write(s"| - | - | - |\n")
    }
    fileWriter.write("|===\n")
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
}

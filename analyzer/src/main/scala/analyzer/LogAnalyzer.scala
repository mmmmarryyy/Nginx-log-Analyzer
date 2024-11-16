package analyzer

import java.time.ZonedDateTime

object LogAnalyzer {
  def analyzeLogRecords(
      logRecords: Iterator[LogRecord],
      currentLogReport: LogReport
  ): LogReport = {
    val (
      totalRequests,
      addresses,
      resources,
      methods,
      statusCodes,
      responseSizes
    ) = logRecords.foldLeft(
      currentLogReport.logStatistics match {
        case Some(statistics) =>
          (
            statistics.totalRequests,
            statistics.topRemoteAddresses,
            statistics.topResources,
            statistics.topMethods,
            statistics.topStatusCodes,
            currentLogReport.responseSizes
          )
        case None =>
          (
            0L,
            Map[String, Long](),
            Map[String, Long](),
            Map[String, Long](),
            Map[Int, Long](),
            currentLogReport.responseSizes
          )
      }
    ) { case ((count, addresses, res, methods, codes, sizes), record) =>
      (
        count + 1,
        addresses.updated(
          record.remoteAddress,
          addresses.getOrElse(record.remoteAddress, 0L) + 1
        ),
        res.updated(record.resource, res.getOrElse(record.resource, 0L) + 1),
        methods.updated(
          record.httpMethod,
          methods.getOrElse(record.httpMethod, 0L) + 1
        ),
        codes.updated(record.status, codes.getOrElse(record.status, 0L) + 1),
        sizes.appended(record.bodyBytesSent)
      )
    }

    if (totalRequests == 0L) currentLogReport
    else {
      val topAddresses = addresses.toSeq.sortBy(-_._2).take(3).toMap
      val topResources = resources.toSeq.sortBy(-_._2).take(3).toMap
      val topMethods = methods.toSeq.sortBy(-_._2).take(3).toMap
      val topStatusCodes = statusCodes.toSeq.sortBy(-_._2).take(3).toMap
      val avgResponseBodySize = responseSizes.sum.toDouble / responseSizes.size
      val p95ResponseBodySize = responseSizes.sorted
        .drop(responseSizes.size * 95 / 100)
        .head

      currentLogReport.copy(
        logStatistics = Some(
          LogStatistics(
            totalRequests,
            topAddresses,
            topResources,
            topStatusCodes,
            topMethods,
            avgResponseBodySize,
            p95ResponseBodySize
          )
        ),
        responseSizes = responseSizes
      )
    }
  }
}

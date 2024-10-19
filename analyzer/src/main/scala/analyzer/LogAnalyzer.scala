package analyzer

import java.time.ZonedDateTime

object LogAnalyzer {
  def analyzeLogRecords(
      logRecords: Iterator[LogRecord],
      path: String,
      from: Option[ZonedDateTime],
      to: Option[ZonedDateTime]
  ): LogReport = {
    val (
      totalRequests,
      addresses,
      resources,
      methods,
      statusCodes,
      responseSizes
    ) =
      logRecords.foldLeft(
        (
          0L,
          Map[String, Long](),
          Map[String, Long](),
          Map[String, Long](),
          Map[Int, Long](),
          List[Int]()
        )
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

    if (totalRequests == 0L) {
      LogReport(
        path,
        from,
        to,
        None,
        None,
        None,
        None,
        None,
        None,
        None
      )
    } else {
      val topAddresses = addresses.toSeq.sortBy(-_._2).take(3).toMap
      val topResources = resources.toSeq.sortBy(-_._2).take(3).toMap
      val topMethods = methods.toSeq.sortBy(-_._2).take(3).toMap
      val topStatusCodes = statusCodes.toSeq.sortBy(-_._2).take(3).toMap
      val avgResponseBodySize = responseSizes.sum.toDouble / responseSizes.size
      val p95ResponseBodySize = responseSizes.sorted
        .drop(responseSizes.size * 95 / 100)
        .head

      LogReport(
        path,
        from,
        to,
        Some(totalRequests),
        Some(topAddresses),
        Some(topResources),
        Some(topStatusCodes),
        Some(topMethods),
        Some(avgResponseBodySize),
        Some(p95ResponseBodySize)
      )
    }
  }
}

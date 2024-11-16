package analyzer

case class LogStatistics(
    totalRequests: Long,
    topRemoteAddresses: Map[String, Long],
    topResources: Map[String, Long],
    topStatusCodes: Map[Int, Long],
    topMethods: Map[String, Long],
    avgResponseBodySize: Double,
    p95ResponseBodySize: Double
)

package analyzer

import java.time.ZonedDateTime

case class LogRecord(
    remoteAddress: String,
    remoteUser: String,
    timeLocal: ZonedDateTime,
    request: String,
    httpMethod: String,
    resource: String,
    status: Int,
    bodyBytesSent: Int,
    httpReferer: String,
    httpUserAgent: String
)

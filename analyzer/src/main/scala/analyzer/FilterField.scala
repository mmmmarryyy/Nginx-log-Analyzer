package analyzer

sealed trait FilterField
case object RemoteAddress extends FilterField
case object RemoteUser extends FilterField
case object HttpMethod extends FilterField
case object Resource extends FilterField
case object Status extends FilterField
case object BodyBytesSent extends FilterField
case object HttpReferer extends FilterField
case object HttpUserAgent extends FilterField

def parseFilterFieldString(formatStr: String): Option[FilterField] =
  formatStr match
    case "address"       => Some(RemoteAddress)
    case "user"          => Some(RemoteUser)
    case "method"        => Some(HttpMethod)
    case "resource"      => Some(Resource)
    case "status"        => Some(Status)
    case "bodyBytesSent" => Some(BodyBytesSent)
    case "referer"       => Some(HttpReferer)
    case "agent"         => Some(HttpUserAgent)
    case _               => None

package org.backend.academy.resources

import scala.io.Source
import scala.util.Using

object SafeResourceExample extends App {
  val content = Using.resource(Source.fromFile("files/example.txt")) { source =>
    source.getLines().mkString("\n")
  }

  println(content)
}

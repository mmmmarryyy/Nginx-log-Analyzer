package org.backend.academy.adt

import scala.math.Pi

sealed trait Shape:
  def area: Double
end Shape

object Shape:
  final case class Circle(radius: Double) extends Shape:
    override def area: Double = Pi * radius * radius
  end Circle
  final case class Rectangle(width: Double, height: Double) extends Shape:
    override def area: Double = width * height
  end Rectangle
  final case class Triangle(base: Double, height: Double) extends Shape:
    override def area: Double = 0.5 * base * height
  end Triangle
end Shape

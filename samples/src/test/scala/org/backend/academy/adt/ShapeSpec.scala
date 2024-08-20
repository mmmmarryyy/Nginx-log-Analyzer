package org.backend.academy.adt

import org.backend.academy.adt.Shape.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ShapeSpec extends AnyFlatSpec with Matchers:
  "A Shape" should "be able to calculate its area" in {
    val shape = Rectangle(10, 20)
    val shape2 = Circle(10)
    val shape3 = Triangle(10, 20)

    shape.area should be(200)
    shape2.area should be(314.1592653589793)
    shape3.area should be(100)
  }
end ShapeSpec

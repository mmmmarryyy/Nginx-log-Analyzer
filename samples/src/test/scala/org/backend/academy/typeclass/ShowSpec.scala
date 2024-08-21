package org.backend.academy.typeclass

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ShowSpec extends AnyFlatSpec with Matchers:
  private case class Person(firstName: String, lastName: String)

  private given Show[Person] with
    extension (p: Person)
      def show: String =
        s"${p.firstName} ${p.lastName}"

  "Show" should "show a value for person" in {
    val person = Person("John", "Doe")
    person.show should be("John Doe")
  }
end ShowSpec

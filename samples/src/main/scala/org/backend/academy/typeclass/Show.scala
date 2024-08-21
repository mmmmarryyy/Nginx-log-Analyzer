package org.backend.academy.typeclass

trait Show[A]:
  extension (a: A) def show: String

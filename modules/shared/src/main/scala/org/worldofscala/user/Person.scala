package org.worldofscala.user

import sttp.tapir.Schema
import zio.json.JsonCodec
import zio.prelude.*
import zio.prelude.Debug.Repr
import zio.prelude.Debug.Renderer
import zio.prelude.magnolia.*

import dev.cheleb.scalamigen.NoPanel

@NoPanel
case class Person(
  firstname: String,
  lastname: String,
  email: String,
  password: Password,
  passwordConfirmation: Password
) derives JsonCodec,
      Schema,
      Debug {
  def errorMessages = {
    val passwordErrors = if password == passwordConfirmation then Nil else List("Passwords do not match")
    passwordErrors
  }
}

opaque type Password <: String = String

object Password:
  given JsonCodec[Password] = JsonCodec.string
  given Schema[Password]    = Schema.string

  given Debug[Password] with
    def debug(value: Password): Repr = Repr.String("*****")

  def apply(password: String): Password = password

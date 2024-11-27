package org.worldofscala.user

import zio.json.JsonCodec
import sttp.tapir.Schema
import java.time.ZonedDateTime
import java.util.UUID
import zio.prelude.*
import zio.prelude.Debug.Repr
import zio.prelude.Debug.Renderer
import zio.prelude.magnolia.*

import dev.cheleb.scalamigen.NoPanel

@NoPanel
case class NewUser(
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

case class User(
  id: UUID,
  firstname: String,
  lastname: String,
  email: String,
  hashedPassword: String,
  creationDate: ZonedDateTime
) derives JsonCodec,
      Schema

case class UserID(id: UUID, email: String) derives JsonCodec, Schema

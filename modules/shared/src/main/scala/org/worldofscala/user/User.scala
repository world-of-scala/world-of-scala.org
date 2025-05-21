package org.worldofscala.user

import zio.json.JsonCodec
import sttp.tapir.Schema
import java.time.ZonedDateTime
import java.util.UUID
import zio.prelude.*
import zio.prelude.Debug.Repr
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
  def errorMessages =
    NewUser.validate(this)
}

object NewUser {
  private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".r

  def isValidEmail(email: String): Boolean =
    emailRegex.matches(email)

  def validate(newUser: NewUser): List[String] = {
    val emailErrors =
      if isValidEmail(newUser.email) then Nil else List("Invalid email format")
    val passwordErrors =
      if newUser.password == newUser.passwordConfirmation then Nil else List("Passwords do not match")
    emailErrors ++ passwordErrors
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
  id: User.Id,
  firstname: String,
  lastname: String,
  email: String,
  hashedPassword: String,
  creationDate: ZonedDateTime
) derives JsonCodec,
      Schema

object User:

  opaque type Id <: UUID = UUID
  object Id:
    def apply(uuid: UUID): Id         = uuid
    def unapply(id: Id): Option[UUID] = Some(id)

//    extension (id: Id) def value: UUID = id

    given JsonCodec[Id] = JsonCodec.uuid
    given Schema[Id]    = Schema.schemaForUUID

    given Debug[Id] with
      def debug(value: Id): Repr = Repr.String(value.toString)

case class UserID(id: User.Id, email: String) derives JsonCodec, Schema

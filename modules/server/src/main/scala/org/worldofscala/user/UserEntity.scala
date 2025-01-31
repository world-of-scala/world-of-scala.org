package org.worldofscala.user

import java.time.ZonedDateTime
import io.scalaland.chimney.Transformer

case class NewUserEntity(
  id: Option[User.Id],
  firstname: String,
  lastname: String,
  email: String,
  hashedPassword: String,
  creationDate: ZonedDateTime
)

case class UserEntity(
  id: User.Id,
  firstname: String,
  lastname: String,
  email: String,
  hashedPassword: String,
  creationDate: ZonedDateTime
)

object UserEntity:
  given Transformer[UserEntity, User] = Transformer.derive

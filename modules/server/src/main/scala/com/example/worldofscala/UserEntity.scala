package com.example.worldofscala

import java.time.ZonedDateTime
import com.example.worldofscala.domain.*
import io.scalaland.chimney.Transformer

case class NewUserEntity(
  id: Option[Long],
  firstname: String,
  lastname: String,
  email: String,
  hashedPassword: String,
  creationDate: ZonedDateTime
)

case class UserEntity(
  id: Long,
  firstname: String,
  lastname: String,
  email: String,
  hashedPassword: String,
  creationDate: ZonedDateTime
)

object UserEntity:
  given Transformer[UserEntity, User] = Transformer.derive

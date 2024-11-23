package org.worldofscala.user

import zio.json.JsonCodec
import sttp.tapir.Schema
import java.time.ZonedDateTime
import java.util.UUID

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

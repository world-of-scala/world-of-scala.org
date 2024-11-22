package com.example.worldofscala.domain

import zio.json.JsonCodec
import sttp.tapir.Schema
import java.time.ZonedDateTime

case class User(
  id: Long,
  firstname: String,
  lastname: String,
  email: String,
  hashedPassword: String,
  creationDate: ZonedDateTime
) derives JsonCodec,
      Schema

case class UserID(id: Long, email: String) derives JsonCodec, Schema

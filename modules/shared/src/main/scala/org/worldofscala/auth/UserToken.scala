package org.worldofscala.auth

import zio.json.JsonCodec

import dev.cheleb.ziojwt.WithToken
import java.util.UUID

final case class UserToken(id: UUID, email: String, token: String, expiration: Long) extends WithToken derives JsonCodec

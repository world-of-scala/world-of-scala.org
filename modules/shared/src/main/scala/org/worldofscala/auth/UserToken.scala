package org.worldofscala.auth

import zio.json.JsonCodec

import dev.cheleb.ziojwt.WithToken
import java.util.UUID
import org.worldofscala.user.User

final case class UserToken(id: User.Id, email: String, token: String, expiration: Long) extends WithToken
    derives JsonCodec

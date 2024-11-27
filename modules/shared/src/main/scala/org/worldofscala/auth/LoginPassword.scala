package org.worldofscala.auth

import sttp.tapir.Schema
import dev.cheleb.scalamigen.NoPanel
import org.worldofscala.user.Password

@NoPanel
final case class LoginPassword(login: String, password: Password) derives zio.json.JsonCodec, Schema:
  def isIncomplete: Boolean = login.isBlank || password.isBlank

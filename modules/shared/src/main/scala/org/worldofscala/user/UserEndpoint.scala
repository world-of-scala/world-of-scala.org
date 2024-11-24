package org.worldofscala.user

import zio.*
import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import org.worldofscala.domain.*
import org.worldofscala.auth.LoginPassword
import sttp.model.HeaderNames
import org.worldofscala.BaseEndpoint
import org.worldofscala.auth.*

object UserEndpoint extends BaseEndpoint:

  val create: PublicEndpoint[NewUser, Throwable, User, Any] = baseEndpoint
    .tag("person")
    .name("person")
    .post
    .in("person")
    .in(
      jsonBody[NewUser]
        .description("Person to create")
        .example(
          NewUser(
            "John",
            "Does",
            "john.doe@foo.bar",
            Password("notsecured"),
            Password("notsecured")
          )
        )
    )
    .out(jsonBody[User])
    .description("Create person")

  val login: PublicEndpoint[LoginPassword, Throwable, UserToken, Any] = baseEndpoint
    .tag("person")
    .name("login")
    .post
    .in("login")
    .in(
      jsonBody[LoginPassword]
    )
    .out(jsonBody[UserToken])
    .description("Login")

  val profile: Endpoint[String, Unit, Throwable, User, Any] = baseSecuredEndpoint
    .tag("person")
    .name("profile")
    .get
    .in("profile")
    .out(jsonBody[User])
    .description("Get profile")

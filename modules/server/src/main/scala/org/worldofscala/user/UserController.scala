package org.worldofscala.user

import dev.cheleb.ziotapir.SecuredBaseController

import zio.*

import sttp.model.Uri
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.ztapir.*
import sttp.capabilities.zio.ZioStreams

import org.worldofscala.domain.errors.NotHostHeaderException
import org.worldofscala.auth.*

class UserController private (personService: UserService, jwtService: JWTService)
    extends SecuredBaseController[String, UserID](jwtService.verifyToken) {

  val create: ServerEndpoint[Any, Task] = UserEndpoint.create
    .zServerLogic:
      personService.register

  val login: ServerEndpoint[Any, Task] = UserEndpoint.login.zServerLogic { lp =>
    for {
      user  <- personService.login(lp.login, lp.password)
      token <- jwtService.createToken(user)
    } yield token
  }

  val profile: ServerEndpoint[Any, Task] = UserEndpoint.profile.securedServerLogic { userId => _ =>
    personService.getProfile(userId)
  }

  val routes: (List[ServerEndpoint[Any, Task]], List[ZServerEndpoint[Any, ZioStreams]]) =
    (List(create, login, profile), List.empty)
}

object UserController {
  def makeZIO: URIO[UserService & JWTService, UserController] =
    for
      jwtService    <- ZIO.service[JWTService]
      personService <- ZIO.service[UserService]
    yield new UserController(personService, jwtService)
}
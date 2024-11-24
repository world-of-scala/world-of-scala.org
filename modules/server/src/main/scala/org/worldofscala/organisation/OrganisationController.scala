package org.worldofscala.organisation

import dev.cheleb.ziotapir.SecuredBaseController

import zio.*

import sttp.model.Uri
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.ztapir.*

import org.worldofscala.domain.errors.NotHostHeaderException
import org.worldofscala.auth.*
import org.worldofscala.user.UserID

class OrganisationController private (organisationService: OrganisationService, jwtService: JWTService)
    extends SecuredBaseController[String, UserID](jwtService.verifyToken) {

  val create: ServerEndpoint[Any, Task] = OrganisationEndpoint.create.securedServerLogic { userId => organisation =>
    organisationService.create(organisation)
  }

  val routes: List[ServerEndpoint[Any, Task]] =
    List(create)

}

object OrganisationController {
  def makeZIO: URIO[OrganisationService & JWTService, OrganisationController] =
    for
      organisationService <- ZIO.service[OrganisationService]
      jwtService          <- ZIO.service[JWTService]
    yield new OrganisationController(organisationService, jwtService)

}

package org.worldofscala.organisation

import dev.cheleb.ziotapir.SecuredBaseController

import zio.*
import zio.json.*

import sttp.model.Uri
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.ztapir.*

import org.worldofscala.domain.errors.NotHostHeaderException
import org.worldofscala.auth.*
import org.worldofscala.user.UserID
import zio.stream.ZStream
import java.util.UUID
import sttp.capabilities.Streams
import sttp.capabilities.zio.ZioStreams

class OrganisationController private (organisationService: OrganisationService, jwtService: JWTService)
    extends SecuredBaseController[String, UserID](jwtService.verifyToken) {

  val create: ServerEndpoint[Any, Task] = OrganisationEndpoint.create.securedServerLogic { userId => organisation =>
    organisationService.create(organisation)
  }

  val listAll: ServerEndpoint[Any, Task] = OrganisationEndpoint.all.zServerLogic { _ =>
    organisationService.listAll()
  }

  val streamAll: ZServerEndpoint[Any, ZioStreams] = OrganisationEndpoint.allStream.zServerLogic { _ =>
    organisationService.streamAll()
  }

  val routes: (List[ServerEndpoint[Any, Task]], List[ZServerEndpoint[Any, ZioStreams]]) =
    (List(create, listAll), List(streamAll))

}

object OrganisationController {
  def makeZIO: URIO[OrganisationService & JWTService, OrganisationController] =
    for
      organisationService <- ZIO.service[OrganisationService]
      jwtService          <- ZIO.service[JWTService]
    yield new OrganisationController(organisationService, jwtService)

}

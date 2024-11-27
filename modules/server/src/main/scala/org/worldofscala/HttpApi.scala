package org.worldofscala

import zio.*
import sttp.tapir.ztapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.capabilities.zio.ZioStreams

import dev.cheleb.ziotapir.BaseController
import org.worldofscala.auth.JWTService
import org.worldofscala.health.HealthController
import org.worldofscala.user.*
import org.worldofscala.organisation.*

import user.*

//https://tapir.softwaremill.com/en/latest/server/logic.html
object HttpApi {
  private def gatherRoutes(
    controllers: List[BaseController]
  ): (List[ServerEndpoint[Any, Task]], List[ZServerEndpoint[Any, ZioStreams]]) =
    controllers.foldLeft((List.empty[ServerEndpoint[Any, Task]], List.empty[ZServerEndpoint[Any, ZioStreams]])) {
      case ((acc1, acc2), controller) =>
        val (routes1, routes2) = controller.routes
        (acc1 ++ routes1, acc2 ++ routes2)
    }

  private def makeControllers = for {
    healthController       <- HealthController.makeZIO
    personController       <- UserController.makeZIO
    organisationController <- OrganisationController.makeZIO
  } yield List(healthController, personController, organisationController)

  val endpointsZIO: URIO[
    UserService & OrganisationService & JWTService,
    (List[ServerEndpoint[Any, Task]], List[ZServerEndpoint[Any, ZioStreams]])
  ] =
    makeControllers.map(gatherRoutes)

//  val streamingEndpointsZIO =
}

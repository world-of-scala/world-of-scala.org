package org.worldofscala

import zio.*
import sttp.tapir.server.ServerEndpoint

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
  ): List[ServerEndpoint[Any, Task]] =
    controllers.flatMap(_.routes)

  private def makeControllers = for {
    healthController       <- HealthController.makeZIO
    personController       <- UserController.makeZIO
    organisationController <- OrganisationController.makeZIO
  } yield List(healthController, personController, organisationController)

  val endpointsZIO: URIO[UserService & OrganisationService & JWTService, List[ServerEndpoint[Any, Task]]] =
    makeControllers.map(gatherRoutes)
}

package org.worldofscala

import zio.*
import sttp.tapir.server.ServerEndpoint

import org.worldofscala.BaseController
import org.worldofscala.auth.JWTService
import org.worldofscala.health.HealthController
import org.worldofscala.user.*

import user.*

//https://tapir.softwaremill.com/en/latest/server/logic.html
object HttpApi {
  private def gatherRoutes(
    controllers: List[BaseController]
  ): List[ServerEndpoint[Any, Task]] =
    controllers.flatMap(_.routes)

  private def makeControllers = for {
    healthController <- HealthController.makeZIO
    personController <- UserController.makeZIO
  } yield List(healthController, personController)

  val endpointsZIO: URIO[UserService & JWTService, List[ServerEndpoint[Any, Task]]] =
    makeControllers.map(gatherRoutes)
}

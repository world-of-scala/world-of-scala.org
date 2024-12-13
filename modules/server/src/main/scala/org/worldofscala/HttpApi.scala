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
import dev.cheleb.ziotapir.*

//https://tapir.softwaremill.com/en/latest/server/logic.html
object HttpApi extends Routes {

  private val makeControllers =
    for {
      healthController       <- HealthController.makeZIO
      personController       <- UserController.makeZIO
      organisationController <- OrganisationController.makeZIO
    } yield List(healthController, personController, organisationController)

  def endpointsZIO: URIO[UserService & JWTService & OrganisationService, List[ServerEndpoint[Any, Task]]] =
    makeControllers.map(gatherRoutes(_.routes))

  def streamEndpointsZIO: URIO[UserService & JWTService & OrganisationService, List[ServerEndpoint[ZioStreams, Task]]] =
    makeControllers.map(gatherRoutes(_.streamRoutes))

  def endpoints = for {
    endpoints       <- endpointsZIO
    streamEndpoints <- streamEndpointsZIO
  } yield endpoints ++ streamEndpoints

//  val streamingEndpointsZIO =
}

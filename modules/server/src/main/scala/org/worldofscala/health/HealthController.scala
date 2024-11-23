package org.worldofscala.health

import zio.*
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import org.worldofscala.http.endpoints.HealthEndpoint
import org.worldofscala.BaseController

class HealthController private extends BaseController with HealthEndpoint {

  val health = healthEndpoint
    .serverLogicSuccess[Task](_ => ZIO.succeed("OK"))
  override val routes: List[ServerEndpoint[Any, Task]] = List(health)
}

object HealthController {
  val makeZIO = ZIO.succeed(new HealthController)
}

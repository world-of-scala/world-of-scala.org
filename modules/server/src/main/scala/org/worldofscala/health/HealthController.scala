package org.worldofscala.health

import zio.*
import sttp.tapir.*
import sttp.tapir.ztapir.*
import sttp.tapir.server.ServerEndpoint
import org.worldofscala.http.endpoints.HealthEndpoint
import dev.cheleb.ziotapir.BaseController
import sttp.capabilities.zio.ZioStreams

class HealthController private extends BaseController with HealthEndpoint {

  val health = healthEndpoint
    .serverLogicSuccess[Task](_ => ZIO.succeed("OK"))
  override val routes: (List[ServerEndpoint[Any, Task]], List[ZServerEndpoint[Any, ZioStreams]]) =
    (List(health), List.empty)
}

object HealthController {
  val makeZIO = ZIO.succeed(new HealthController)
}

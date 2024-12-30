package org.worldofscala.earth

import org.worldofscala.auth.JWTService
import dev.cheleb.ziotapir.SecuredBaseController
import org.worldofscala.user.UserID

import zio.*

import sttp.tapir.server.*
import sttp.tapir.ztapir.*
import sttp.capabilities.zio.ZioStreams

class MeshController private (meshService: MeshService, jwtService: JWTService)
    extends SecuredBaseController[String, UserID](jwtService.verifyToken) {

//   val create: ServerEndpoint[Any, Task] = MeshEndpoint.create.zServerAuthenticatedLogic { userId => mesh =>
//     meshService.create(mesh)
//   }

  val listAll: ServerEndpoint[Any, Task] = MeshEndpoint.all.zServerLogic { _ =>
    meshService.listAll()
  }

  val createStream: ZServerEndpoint[Any, ZioStreams] = MeshEndpoint.streamCreate.zServerAuthenticatedLogic {
    userId => (name, stream) =>
      meshService.createStream(name, stream)
  }

//   val streamAll: ZServerEndpoint[Any, ZioStreams] = MeshEndpoint.allStream.zServerLogic { _ =>
//     meshService.streamAll()
//   }

  override val routes: List[ServerEndpoint[Any, Task]] =
    List(listAll)

  override def streamRoutes: List[ServerEndpoint[ZioStreams, Task]] = List(createStream)

}

object MeshController {
  def makeZIO: URIO[MeshService & JWTService, MeshController] =
    for
      meshService <- ZIO.service[MeshService]
      jwtService  <- ZIO.service[JWTService]
    yield new MeshController(meshService, jwtService)

}

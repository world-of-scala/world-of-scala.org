package org.worldofscala.earth

import sttp.tapir.*

import org.worldofscala.BaseEndpoint

import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*

object MeshEndpoint extends BaseEndpoint {

  val streamCreate = baseSecuredEndpoint
    .tag("Admin")
    .name("mesh")
    .post
    .in("mesh" / path[String]("name"))
    .in(
      inputStreamBody
        .description("Mesh to insert")
    )
    .out(jsonBody[Mesh.Id])
    .description("Create mesh")

  val all: PublicEndpoint[Unit, Throwable, List[(Mesh.Id, String)], Any] = baseEndpoint
    .tag("Admin")
    .name("mesh")
    .get
    .in("mesh")
    .out(jsonBody[List[(Mesh.Id, String)]])
    .description("Get all meshes")

//   val allStream: Endpoint[Unit, Unit, Throwable, Stream[Throwable, Byte], ZioStreams] = baseEndpoint
//     .tag("Admin")
//     .name("mesh stream")
//     .get
//     .in("mesh" / "stream")
//     .out(streamBody(ZioStreams)(Schema.derived[(Mesh.Id, String)], CodecFormat.TextEventStream()))
//     .description("Get all meshes")

}

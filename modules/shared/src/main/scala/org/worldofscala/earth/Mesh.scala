package org.worldofscala.earth

import java.util.UUID
import zio.json.JsonCodec
import sttp.tapir.Schema

final case class Mesh(id: Mesh.Id, label: String, blob: Array[Byte]) derives JsonCodec, Schema

object Mesh:
  opaque type Id <: UUID = UUID

  object Id:
    def apply(uuid: UUID): Id         = uuid
    def unapply(id: Id): Option[UUID] = Some(id)

    given JsonCodec[Id] = JsonCodec.uuid
    given Schema[Id]    = Schema.schemaForUUID

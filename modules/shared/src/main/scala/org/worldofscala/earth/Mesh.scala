package org.worldofscala.earth

import java.util.UUID
import zio.json.JsonCodec
import sttp.tapir.Schema
import zio.prelude.Debug
import zio.prelude.Debug.Repr

import sttp.tapir.Codec

final case class Mesh(id: Mesh.Id, label: String, blob: Array[Byte]) derives JsonCodec, Schema

object Mesh:

  def default = Mesh.Id(UUID.fromString("00000000-0000-0000-0000-000000000000"))

  def defaulEntry = (default, "Scala", None, 0L)

  opaque type Id <: UUID = UUID

  object Id:
    def apply(uuid: UUID): Id         = uuid
    def unapply(id: Id): Option[UUID] = Some(id)

    given JsonCodec[Id]        = JsonCodec.uuid
    given Schema[Id]           = Schema.schemaForUUID
    given Codec.PlainCodec[Id] = Codec.uuid

    given Debug[Id] with
      def debug(value: Id): Repr = Repr.String(value.toString)

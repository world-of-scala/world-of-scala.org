package org.worldofscala.earth

import java.util.UUID
import zio.json.JsonCodec
import sttp.tapir.Schema

import sttp.tapir.Codec

import org.worldofscala.UUIDOpaque

final case class Mesh(id: Mesh.Id, label: String, blob: Array[Byte]) derives JsonCodec, Schema

object Mesh:

  def default = Mesh.Id(UUID.fromString("00000000-0000-0000-0000-000000000000"))

  def defaulEntry = (default, "Scala", None, 0L)

  opaque type Id <: UUID = UUID

  object Id extends UUIDOpaque[Id](JsonCodec.uuid, Schema.schemaForUUID):
    def apply(uuid: UUID): Id = uuid

    given Codec.PlainCodec[Id] = Codec.uuid

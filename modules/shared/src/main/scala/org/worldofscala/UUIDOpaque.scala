package org.worldofscala

import zio.json.JsonCodec
import sttp.tapir.Schema
import zio.prelude.Debug.Repr
import zio.prelude.*
import java.util.UUID

trait UUIDOpaque[ID <: UUID](json: JsonCodec[ID], schema: Schema[ID]):
  given JsonCodec[ID] = json
  given Schema[ID]    = schema

  given Debug[ID] with
    def debug(value: ID): Repr = Repr.String(value.toString)

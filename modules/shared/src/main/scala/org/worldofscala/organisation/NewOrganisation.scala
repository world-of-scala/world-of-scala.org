package org.worldofscala.organisation

import java.util.UUID
import java.time.ZonedDateTime

import sttp.tapir.Schema
import zio.json.JsonCodec
import zio.prelude.*
import zio.prelude.magnolia.*

import dev.cheleb.scalamigen.NoPanel
import zio.prelude.Debug.Repr

@NoPanel
case class NewOrganisation(
  name: String,
  location: Option[LatLon]
) derives JsonCodec,
      Schema,
      Debug:
  def errorMessages: Seq[String] = Seq.empty

@NoPanel
case class LatLon(lat: Double, lon: Double) derives JsonCodec, Schema, Debug

case class Organisation(
  id: UUID,
  name: String,
  location: Option[LatLon]
) derives JsonCodec,
      Schema,
      Debug:
  def errorMessages: Seq[String] = Seq.empty

object Organisation:
  given Debug[UUID] with
    def debug(value: UUID): Repr = Repr.String(value.toString)

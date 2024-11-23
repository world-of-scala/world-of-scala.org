package org.worldofscala.organisation

import java.util.UUID
import java.time.ZonedDateTime

import sttp.tapir.Schema
import zio.json.JsonCodec
import zio.prelude.*
import zio.prelude.magnolia.*

import dev.cheleb.scalamigen.NoPanel

@NoPanel
case class Organisation(
  name: String,
  location: Option[LatLon]
) derives JsonCodec,
      Schema,
      Debug:
  def errorMessages: Seq[String] = Seq.empty
  val o                          = 1

@NoPanel
case class LatLon(lat: Double, lon: Double) derives JsonCodec, Schema, Debug

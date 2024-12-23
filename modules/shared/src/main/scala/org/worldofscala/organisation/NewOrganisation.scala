package org.worldofscala.organisation

import java.util.UUID
import java.time.ZonedDateTime

import sttp.tapir.Schema
import zio.json.JsonCodec
import zio.prelude.*
import zio.prelude.magnolia.*

import dev.cheleb.scalamigen.NoPanel
import zio.prelude.Debug.Repr
import Math.{PI, cos, sin}

@NoPanel
case class NewOrganisation(
  name: String,
  location: LatLon
) derives JsonCodec,
      Schema,
      Debug:
  def errorMessages: Seq[String] = Seq.empty

/**
 * LatLon
 *
 * @param lat
 * @param lon
 */
@NoPanel
case class LatLon(lat: Double, lon: Double) derives JsonCodec, Schema, Debug:
  override def toString(): String = s"$lat,$lon"

  /**
   * Convert lat lon to xyz
   *
   * @param d
   *   earth radius
   * @return
   */
  def xyz(d: Double) = {
    val latRad = lat * PI / 180.0;
    val lonRad = (-lon + 180) * PI / 180.0;
    (d * cos(latRad) * cos(lonRad), d * sin(latRad), d * cos(latRad) * sin(lonRad));

  }

object LatLon:
  val empty: LatLon = LatLon(0.0, 0.0)

case class Organisation(
  id: Organisation.Id,
  createdBy: UUID,
  name: String,
  location: LatLon
) derives JsonCodec,
      Schema,
      Debug:
  def errorMessages: Seq[String] = Seq.empty

object Organisation:
  given Debug[UUID] with
    def debug(value: UUID): Repr = Repr.String(value.toString)

  opaque type Id = UUID

  object Id:
    def apply(uuid: UUID): Id         = uuid
    def unapply(id: Id): Option[UUID] = Some(id)

    extension (id: Id) def value: UUID = id

    given JsonCodec[Id] = JsonCodec.uuid
    given Schema[Id]    = Schema.string

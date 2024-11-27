package org.worldofscala.organisation

import zio.*
import zio.json.*
import zio.stream.*
import dev.cheleb.ziochimney.*
import io.scalaland.chimney.dsl._
import org.scalafmt.config.Indents.RelativeToLhs.`match`
import zio.stream.ZStream

trait OrganisationService {
  def create(organisation: NewOrganisation): Task[Organisation]
  def listAll(): Task[List[Organisation]]
  def streamAll(): Task[ZStream[Any, Throwable, Byte]]

}

case class OrganisationServiceLive(organisationRepository: OrganisationRepository) extends OrganisationService {

  override def streamAll(): Task[ZStream[Any, Throwable, Byte]] =
    ZIO.succeed(
      organisationRepository
        .streamAll()
        .flatMap(entity =>
          ZStream.fromIterable(
            (entity
              .into[Organisation]
              .withFieldComputed(_.location, e => e.lat.flatMap(lat => e.long.map(long => LatLon(lat, long))))
              .transform
              .toJson + "\n").getBytes
          )
        )
    )

  override def listAll(): Task[List[Organisation]] = organisationRepository
    .listAll()
    .map(entities =>
      entities.map(entity =>
        entity
          .into[Organisation]
          .withFieldComputed(_.location, e => e.lat.flatMap(lat => e.long.map(long => LatLon(lat, long))))
          .transform
      )
    )

  override def create(organisation: NewOrganisation): Task[Organisation] =

    val organisationEntity = organisation.location match
      case Some(location) =>
        NewOrganisationEntity(
          name = organisation.name,
          lat = Some(location.lat),
          long = Some(location.lon)
        )
      case None =>
        NewOrganisationEntity(
          name = organisation.name
        )

    organisationRepository
      .create(organisationEntity)
      .map(entity =>
        entity
          .into[Organisation]
          .withFieldComputed(_.location, e => e.lat.flatMap(lat => e.long.map(long => LatLon(lat, lat))))
          .transform
      )
}

object OrganisationServiceLive:
  def layer: URLayer[OrganisationRepository, OrganisationService] = ZLayer.derive[OrganisationServiceLive]

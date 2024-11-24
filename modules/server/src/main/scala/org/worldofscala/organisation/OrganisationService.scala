package org.worldofscala.organisation

import zio.*
import dev.cheleb.ziochimney.*
import io.scalaland.chimney.dsl._
import org.scalafmt.config.Indents.RelativeToLhs.`match`

trait OrganisationService {
  def create(organisation: NewOrganisation): Task[Organisation]

}

case class OrganisationServiceLive(organisationRepository: OrganisationRepository) extends OrganisationService {
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

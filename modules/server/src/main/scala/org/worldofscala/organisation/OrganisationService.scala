package org.worldofscala.organisation

import zio.*
import zio.json.*
import zio.stream.*
import io.scalaland.chimney.dsl._
import zio.stream.ZStream
import org.worldofscala.user.User

trait OrganisationService {
  def create(organisation: NewOrganisation, userUUID: User.Id): Task[Organisation]
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
          .transform
      )
    )

  override def create(organisation: NewOrganisation, userUUID: User.Id): Task[Organisation] =

    val organisationEntity =
      NewOrganisationEntity(
        createdBy = userUUID,
        name = organisation.name,
        location = organisation.location
      )

    organisationRepository
      .create(organisationEntity)
      .map(entity =>
        entity
          .into[Organisation]
          .transform
      )
}

object OrganisationServiceLive:
  def layer: URLayer[OrganisationRepository, OrganisationService] = ZLayer.derive[OrganisationServiceLive]

package org.worldofscala

import java.util.UUID
import java.time.ZonedDateTime

final case class NewOrganisationEntity(
  id: Option[UUID],
  name: String,
  meshId: Option[UUID],
  lat: Option[Double],
  long: Option[Double],
  creationDate: ZonedDateTime
)

final case class OrganisationEntity(
  id: UUID,
  name: String,
  meshId: Option[UUID],
  lat: Option[Double],
  long: Option[Double],
  creationDate: ZonedDateTime
)

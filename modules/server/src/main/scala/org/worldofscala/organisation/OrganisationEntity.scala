package org.worldofscala.organisation

import java.util.UUID
import java.time.ZonedDateTime

final case class NewOrganisationEntity(
  id: Option[UUID] = None,
  name: String,
  meshId: Option[UUID] = None,
  location: LatLon,
  creationDate: Option[ZonedDateTime] = None
)

final case class OrganisationEntity(
  id: UUID,
  name: String,
  meshId: Option[UUID],
  location: LatLon,
  creationDate: ZonedDateTime
)

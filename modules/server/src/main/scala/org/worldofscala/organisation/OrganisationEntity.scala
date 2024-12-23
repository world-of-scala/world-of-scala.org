package org.worldofscala.organisation

import java.util.UUID
import java.time.ZonedDateTime
import io.getquill.MappedEncoding

final case class NewOrganisationEntity(
  id: Option[Organisation.Id] = None,
  createdBy: UUID,
  name: String,
  meshId: Option[UUID] = None,
  location: LatLon,
  creationDate: Option[ZonedDateTime] = None
)

final case class OrganisationEntity(
  id: Organisation.Id,
  createdBy: UUID,
  name: String,
  meshId: Option[UUID],
  location: LatLon,
  creationDate: ZonedDateTime
)

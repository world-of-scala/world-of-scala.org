package org.worldofscala.organisation

import java.util.UUID
import java.time.ZonedDateTime
import org.worldofscala.user.User

final case class NewOrganisationEntity(
  id: Option[Organisation.Id] = None,
  createdBy: User.Id,
  name: String,
  meshId: Option[UUID] = None,
  location: LatLon,
  creationDate: Option[ZonedDateTime] = None
)

final case class OrganisationEntity(
  id: Organisation.Id,
  createdBy: User.Id,
  name: String,
  meshId: Option[UUID],
  location: LatLon,
  creationDate: ZonedDateTime
)

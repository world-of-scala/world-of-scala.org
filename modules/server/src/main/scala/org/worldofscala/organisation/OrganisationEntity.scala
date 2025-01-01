package org.worldofscala.organisation

import java.time.ZonedDateTime
import org.worldofscala.user.User
import org.worldofscala.earth.Mesh

final case class NewOrganisationEntity(
  id: Option[Organisation.Id] = None,
  createdBy: User.Id,
  name: String,
  meshId: Option[Mesh.Id] = None,
  location: LatLon,
  creationDate: Option[ZonedDateTime] = None
)

final case class OrganisationEntity(
  id: Organisation.Id,
  createdBy: User.Id,
  name: String,
  meshId: Option[Mesh.Id],
  location: LatLon,
  creationDate: ZonedDateTime
)

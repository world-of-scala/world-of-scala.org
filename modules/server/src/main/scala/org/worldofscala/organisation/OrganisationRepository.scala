package org.worldofscala.organisation

import io.getquill.jdbczio.Quill
import org.worldofscala.*
import io.getquill.*
import io.scalaland.chimney.dsl.*

import zio.*
import zio.stream.ZStream
import org.worldofscala.repository.PGpointSupport

import org.worldofscala.user.UserRepository
import org.worldofscala.earth.MeshRepository

import org.worldofscala.repository.UUIDMapper

trait OrganisationRepository {
  def create(org: NewOrganisationEntity): Task[OrganisationEntity]
  def listAll(): Task[List[OrganisationEntity]]
  def streamAll(): ZStream[Any, Throwable, OrganisationEntity]
}

object OrganisationRepository extends UUIDMapper[Organisation.Id](identity, Organisation.Id.apply)

class OrganisationRepositoryLive private (val quill: Quill.Postgres[SnakeCase])
    extends OrganisationRepository
    with PGpointSupport {
  import quill.*

  import OrganisationRepository.given
  import MeshRepository.given
  import UserRepository.given
  override def streamAll(): ZStream[Any, Throwable, OrganisationEntity] =
    stream(query[OrganisationEntity])

  inline given SchemaMeta[NewOrganisationEntity] = schemaMeta[NewOrganisationEntity]("organisations")
  inline given InsertMeta[NewOrganisationEntity] = insertMeta[NewOrganisationEntity](_.id, _.creationDate)
  inline given SchemaMeta[OrganisationEntity]    = schemaMeta[OrganisationEntity]("organisations")
  inline given UpdateMeta[OrganisationEntity]    = updateMeta[OrganisationEntity](_.id, _.creationDate, _.createdBy)

  override def create(orga: NewOrganisationEntity): Task[OrganisationEntity] =
    run(query[NewOrganisationEntity].insertValue(lift(orga)).returning(r => r))
      .map(r => r.intoPartial[OrganisationEntity].transform.asOption)
      .someOrFail(new RuntimeException(""))

  override def listAll(): Task[List[OrganisationEntity]] = run(query[OrganisationEntity])
}

object OrganisationRepositoryLive {
  def layer: URLayer[Quill.Postgres[SnakeCase], OrganisationRepository] =
    ZLayer.derive[OrganisationRepositoryLive]
}

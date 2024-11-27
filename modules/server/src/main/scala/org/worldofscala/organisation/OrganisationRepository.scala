package org.worldofscala.organisation

import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import org.worldofscala.*
import io.getquill.*
import io.scalaland.chimney.dsl.*

import zio.*
import zio.json.*
import zio.stream.ZStream

trait OrganisationRepository {
  def create(org: NewOrganisationEntity): Task[OrganisationEntity]
  def listAll(): Task[List[OrganisationEntity]]
  def streamAll(): ZStream[Any, Throwable, OrganisationEntity]
}

class OrganisationRepositoryLive private (quill: Quill.Postgres[SnakeCase]) extends OrganisationRepository {
  import quill.*

  override def streamAll(): ZStream[Any, Throwable, OrganisationEntity] =
    stream(query[OrganisationEntity])

  inline given SchemaMeta[NewOrganisationEntity] = schemaMeta[NewOrganisationEntity]("organisations")
  inline given InsertMeta[NewOrganisationEntity] = insertMeta[NewOrganisationEntity](_.id, _.creationDate)
  inline given SchemaMeta[OrganisationEntity]    = schemaMeta[OrganisationEntity]("organisations")
  inline given UpdateMeta[OrganisationEntity]    = updateMeta[OrganisationEntity](_.id, _.creationDate)

  override def create(user: NewOrganisationEntity): Task[OrganisationEntity] =
    run(query[NewOrganisationEntity].insertValue(lift(user)).returning(r => r))
      .map(r => r.intoPartial[OrganisationEntity].transform.asOption)
      .someOrFail(new RuntimeException(""))

  override def listAll(): Task[List[OrganisationEntity]] = run(query[OrganisationEntity])
}

object OrganisationRepositoryLive {
  def layer: URLayer[Quill.Postgres[SnakeCase], OrganisationRepository] =
    ZLayer.derive[OrganisationRepositoryLive]
}

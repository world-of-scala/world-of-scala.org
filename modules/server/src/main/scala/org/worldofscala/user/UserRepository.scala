package org.worldofscala.user

import zio.*

import org.worldofscala.user.UserEntity
import org.worldofscala.user.NewUserEntity

import io.getquill.*
import io.getquill.jdbczio.*
import io.getquill.jdbczio.Quill.Postgres
import io.scalaland.chimney.dsl.*

import io.getquill.jdbczio.Quill.DataSource
import io.getquill.context.jdbc.PostgresJdbcTypes
import java.util.UUID

trait UserRepository {
  def create(user: NewUserEntity): Task[UserEntity]
  def getById(id: User.Id): Task[Option[UserEntity]]
  def findByEmail(email: String): Task[Option[UserEntity]]
  def update(id: User.Id, op: UserEntity => UserEntity): Task[UserEntity]
  def delete(id: User.Id): Task[UserEntity]
}

object UserRepository:
  given MappedEncoding[User.Id, UUID] =
    MappedEncoding[User.Id, UUID](identity)
  given MappedEncoding[UUID, User.Id] =
    MappedEncoding[UUID, User.Id](User.Id.apply)

class UserRepositoryLive private (quill: Quill.Postgres[SnakeCase]) extends UserRepository {

  import quill.*

  inline given SchemaMeta[NewUserEntity] = schemaMeta[NewUserEntity]("users")
  inline given InsertMeta[NewUserEntity] = insertMeta[NewUserEntity](_.id)
  inline given SchemaMeta[UserEntity]    = schemaMeta[UserEntity]("users")
  inline given UpdateMeta[UserEntity]    = updateMeta[UserEntity](_.id, _.creationDate)

  import UserRepository.given

  override def create(user: NewUserEntity): Task[UserEntity] =
    run(query[NewUserEntity].insertValue(lift(user)).returning(r => r))
      .map(r => r.intoPartial[UserEntity].transform.asOption)
      .someOrFail(new RuntimeException(""))
  override def getById(id: User.Id): Task[Option[UserEntity]] =
    run(query[UserEntity].filter(_.id == lift(id))).map(_.headOption)
  override def findByEmail(email: String): Task[Option[UserEntity]] =
    run(query[UserEntity].filter(_.email == lift(email))).map(_.headOption)

  override def update(id: User.Id, op: UserEntity => UserEntity): Task[UserEntity] =
    for {
      userEntity <- getById(id).someOrFail(new RuntimeException(s"User $id not found"))
      updated <-
        run(
          query[UserEntity].filter(_.id == lift(userEntity.id)).updateValue(lift(op(userEntity))).returning(r => r)
        )
    } yield updated

  override def delete(id: User.Id): Task[UserEntity] =
    run(query[UserEntity].filter(_.id == lift(id)).delete.returning(r => r))
}

object UserRepositoryLive {
  def layer: RLayer[Postgres[SnakeCase], UserRepository] = ZLayer.derive[UserRepositoryLive]
}

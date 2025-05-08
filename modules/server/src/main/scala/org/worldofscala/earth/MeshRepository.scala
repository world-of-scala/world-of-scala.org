package org.worldofscala.earth

import io.getquill.*
import io.getquill.jdbczio.Quill
import java.util.UUID
import org.worldofscala.earth.Mesh.Id

import zio.*
import io.scalaland.chimney.dsl.*
import org.worldofscala.organisation.OrganisationEntity

trait MeshRepository:
  def get(id: Mesh.Id): Task[Option[MeshEntity]]
  def saveMesh(mesh: NewMeshEntity): Task[MeshEntity]
//   def deleteMesh(id: Mesh.Id): Unit
  def updateThumbnail(id: Mesh.Id, thumbnail: Option[String]): Task[Unit]
  def listMeshes(): Task[List[(Mesh.Id, String, Option[String], Long)]]

class MeshRepositoryLive private (val quill: Quill.Postgres[SnakeCase]) extends MeshRepository:

  import quill.*

  given MappedEncoding[Mesh.Id, UUID] =
    MappedEncoding[Mesh.Id, UUID](identity)
  given MappedEncoding[UUID, Mesh.Id] =
    MappedEncoding[UUID, Mesh.Id](Mesh.Id.apply)

  inline given SchemaMeta[NewMeshEntity]      = schemaMeta[NewMeshEntity]("meshes")
  inline given InsertMeta[NewMeshEntity]      = insertMeta[NewMeshEntity](_.id)
  inline given SchemaMeta[MeshEntity]         = schemaMeta[MeshEntity]("meshes")
  inline given SchemaMeta[OrganisationEntity] = schemaMeta[OrganisationEntity]("organisations")
  inline given UpdateMeta[MeshEntity]         = updateMeta[MeshEntity](_.id)

  transparent inline given TransformerConfiguration[?] =
    TransformerConfiguration.default.enableOptionDefaultsToNone
  override def saveMesh(mesh: NewMeshEntity): Task[MeshEntity] =
    run(query[NewMeshEntity].insertValue(lift(mesh)).returning(r => r))
      .map(r => r.intoPartial[MeshEntity].transform.asOption)
      .someOrFail(new RuntimeException(""))

  override def updateThumbnail(id: Mesh.Id, thumbnail: Option[String]): Task[Unit] =
    run(query[MeshEntity].filter(_.id == lift(id)).update(m => m.thumbnail -> lift(thumbnail))).unit

  override def get(id: Id): Task[Option[MeshEntity]] =
    run(query[MeshEntity].filter(_.id == lift(id))).map(_.headOption)

  override def listMeshes(): Task[List[(Mesh.Id, String, Option[String], Long)]] =
    run(quote(for {
      meshes <- query[MeshEntity]
      organisations <- query[OrganisationEntity]
                         .leftJoin(org => org.meshId == Some(meshes.id))
                         .groupBy(o => (meshes.id, meshes.label, meshes.thumbnail, o.map(_.meshId)))
                         .map { case (_, orgs) =>
                           orgs.size
                         }

    } yield {
      (meshes.id, meshes.label, meshes.thumbnail, organisations)
    }))

//    run(query[MeshEntity].map(mesh => (mesh.id, mesh.label, mesh.thumbnail)))

object MeshRepositoryLive:
  def layer: URLayer[Quill.Postgres[SnakeCase], MeshRepository] =
    ZLayer.derive[MeshRepositoryLive]

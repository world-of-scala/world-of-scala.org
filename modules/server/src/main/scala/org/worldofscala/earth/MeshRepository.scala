package org.worldofscala.earth

import io.getquill.*
import io.getquill.jdbczio.Quill
import java.util.UUID
import org.worldofscala.earth.Mesh.Id

import zio.*
import io.scalaland.chimney.dsl.*
import zio.stream.ZStream

trait MeshRepository:
  def get(id: Mesh.Id): Task[Option[MeshEntity]]
  def saveMesh(mesh: NewMeshEntity): Task[MeshEntity]
//   def deleteMesh(id: Mesh.Id): Unit
//   def updateMesh(mesh: Mesh): Unit
  def listMeshes(): Task[List[(Mesh.Id, String)]]

class MeshRepositoryLive private (val quill: Quill.Postgres[SnakeCase]) extends MeshRepository:

  import quill.*

  given MappedEncoding[Mesh.Id, UUID] =
    MappedEncoding[Mesh.Id, UUID](identity)
  given MappedEncoding[UUID, Mesh.Id] =
    MappedEncoding[UUID, Mesh.Id](Mesh.Id.apply)

  inline given SchemaMeta[NewMeshEntity] = schemaMeta[NewMeshEntity]("meshes")
  inline given InsertMeta[NewMeshEntity] = insertMeta[NewMeshEntity](_.id)
  inline given SchemaMeta[MeshEntity]    = schemaMeta[MeshEntity]("meshes")
  inline given UpdateMeta[MeshEntity]    = updateMeta[MeshEntity](_.id)

  override def saveMesh(mesh: NewMeshEntity): Task[MeshEntity] =
    run(query[NewMeshEntity].insertValue(lift(mesh)).returning(r => r))
      .map(r => r.intoPartial[MeshEntity].transform.asOption)
      .someOrFail(new RuntimeException(""))

  override def get(id: Id): Task[Option[MeshEntity]] =
    run(query[MeshEntity].filter(_.id == lift(id))).map(_.headOption)

  override def listMeshes(): Task[List[(Mesh.Id, String)]] =
    run(query[MeshEntity].map(mesh => (mesh.id, mesh.label)))

object MeshRepositoryLive:
  def layer: URLayer[Quill.Postgres[SnakeCase], MeshRepository] =
    ZLayer.derive[MeshRepositoryLive]

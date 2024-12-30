package org.worldofscala.earth

import zio.*
import java.io.InputStream

trait MeshService:
  def createStream(name: String, stream: InputStream): Task[Mesh.Id]
  def listAll(): Task[List[(Mesh.Id, String)]]

case class MeshServiceLive(meshRepository: MeshRepository) extends MeshService {
  // def create(mesh: NewMesh): Task[Mesh] =
  //   val newMeshEntity = NewMeshEntity(None, mesh.name, Files.readAllBytes(mesh.data.body.toPath()))
  //   meshRepository.saveMesh(newMeshEntity).map(_.into[Mesh].transform)

  def createStream(name: String, stream: InputStream): Task[Mesh.Id] =
    val newMeshEntity = NewMeshEntity(None, name, stream.readAllBytes())
    meshRepository.saveMesh(newMeshEntity).map(_.id)
  def listAll(): Task[List[(Mesh.Id, String)]] = meshRepository.listMeshes()
//   def streamAll(): Stream[Throwable, Byte] = ???

}

object MeshServiceLive:
  def layer: ZLayer[MeshRepository, Nothing, MeshService] = ZLayer.derive[MeshServiceLive]

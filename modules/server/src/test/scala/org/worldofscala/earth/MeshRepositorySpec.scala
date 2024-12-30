package org.worldofscala.repository

import zio.*
import zio.test.Assertion.*
import zio.test.*

import org.worldofscala.earth.*
import org.worldofscala.Repository

object MeshRepositorySpec extends ZIOSpecDefault with RepositorySpec("sql/meshes.sql") {

  private val scalaMesh =
    NewMeshEntity(
      None,
      "Scala",
      Array.emptyByteArray
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("CompanyRepositorySpec")(
      test("create a company") {
        val program = for {
          repository <- ZIO.service[MeshRepository]
          meshEntity <- repository.saveMesh(scalaMesh)

        } yield meshEntity

        program.map(meshEntity => assert(meshEntity.label)(equalTo(scalaMesh.label)))
      },
      test("list companies") {
        val program = for {
          repository   <- ZIO.service[MeshRepository]
          _            <- repository.saveMesh(scalaMesh)
          meshEntities <- repository.listMeshes()
        } yield meshEntities

        program.map(meshEntities => assert(meshEntities.size)(equalTo(1)))
      }
    ).provide(
      MeshRepositoryLive.layer,
      Repository.quillLayer,
      dataSouurceLayer,
      Scope.default
    )

}

package org.worldofscala.repository

import zio.*

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres
import javax.sql.DataSource
import java.util.UUID
import io.getquill.MappedEncoding

object Repository {

  def quillLayer: URLayer[DataSource, Postgres[SnakeCase.type]] = Quill.Postgres.fromNamingStrategy(SnakeCase)

  private def datasourceLayer: TaskLayer[DataSource] = Quill.DataSource.fromPrefix("db")

  def dataLayer: TaskLayer[Postgres[SnakeCase.type]] = datasourceLayer >>> quillLayer
}

trait UUIDMapper[A](a2id: A => UUID, id2a: UUID => A) {
  given MappedEncoding[A, UUID] =
    MappedEncoding[A, UUID](a2id)
  given MappedEncoding[UUID, A] =
    MappedEncoding[UUID, A](id2a)

}

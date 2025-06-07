package org.worldofscala

import zio.*
import zio.logging.LogFormat
import zio.logging.backend.SLF4J

import org.worldofscala.config.ServerConfig
import org.worldofscala.http.Server
import org.worldofscala.services.*
import org.worldofscala.repository.Repository

object HttpServer extends ZIOAppDefault:

  override val bootstrap = SLF4J.slf4j(LogFormat.colored)

  private def program = for {
    _ <- FlywayService.runMigrations
    _ <- Server.start
  } yield ()

  override def run: Task[Unit] =
    program
      .provide(
        ServerConfig.layer,
        FlywayServiceLive.configuredLayer

        // , ZLayer.Debug.mermaid
      )

end HttpServer

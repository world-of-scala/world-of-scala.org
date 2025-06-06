package org.worldofscala

import zio.*
import zio.logging.LogFormat
import zio.logging.backend.SLF4J

import org.worldofscala.services.*
import org.worldofscala.user.UserRepositoryLive
import org.worldofscala.user.UserServiceLive
import org.worldofscala.auth.JWTServiceLive
import org.worldofscala.organisation.*
import org.worldofscala.config.*
import org.worldofscala.earth.MeshServiceLive
import org.worldofscala.earth.MeshRepositoryLive
import org.worldofscala.http.Server

object HttpServer extends ZIOAppDefault {

  override val bootstrap = SLF4J.slf4j(LogFormat.colored)

  private val program = for {
    _ <- FlywayService.runMigrations
    _ <- Server.start
  } yield ()

  override def run: Task[Unit] =
    program
      .provide(
        ServerConfig.layer,
        // Service layers
        UserServiceLive.layer,
        OrganisationServiceLive.layer,
        MeshServiceLive.layer,
        FlywayServiceLive.configuredLayer,
        JWTServiceLive.configuredLayer,
        // Repository layers
        UserRepositoryLive.layer,
        OrganisationRepositoryLive.layer,
        MeshRepositoryLive.layer,
        Repository.dataLayer
        // , ZLayer.Debug.mermaid
      )
}

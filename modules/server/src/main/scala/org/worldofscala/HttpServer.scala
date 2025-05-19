package org.worldofscala

import zio.*
import zio.http.*
import zio.logging.LogFormat
import zio.logging.backend.SLF4J

import sttp.tapir.*
import sttp.tapir.files.*
import sttp.tapir.server.ziohttp.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.interceptor.cors.CORSInterceptor

import org.worldofscala.observability.*
import org.worldofscala.services.*
import org.worldofscala.user.UserRepositoryLive
import org.worldofscala.user.UserServiceLive
import org.worldofscala.auth.JWTServiceLive
import org.worldofscala.organisation.*

import org.worldofscala.user.UserService
import org.worldofscala.config.*
import org.worldofscala.earth.MeshServiceLive
import org.worldofscala.earth.MeshRepositoryLive

object HttpServer extends ZIOAppDefault {

  override val bootstrap = SLF4J.slf4j(LogFormat.colored)

  private val webJarRoutes = staticResourcesGetServerEndpoint[Task]("public")(
    this.getClass.getClassLoader,
    "public"
  )

  private def serverOptions: ZioHttpServerOptions[Any] =
    ZioHttpServerOptions.customiseInterceptors
      .metricsInterceptor(metricsInterceptor)
      .appendInterceptor(
        CORSInterceptor.default
      )
      .options

  private def server = for {
    apiEndpoints <- HttpApi.endpoints
    docEndpoints = SwaggerInterpreter()
                     .fromServerEndpoints(apiEndpoints, "World of scala", "1.0.0")
    _ <- Server.serve(
           Routes(
             Method.GET / Root -> handler(Response.redirect(url"public/index.html"))
           ) ++
             ZioHttpInterpreter(serverOptions)
               .toHttp(metricsEndpoint :: webJarRoutes :: apiEndpoints ::: docEndpoints)
         ) <* Console.printLine("Server started !")
  } yield ()

  private val program = for {
    _            <- FlywayService.runMigrations
    serverConfig <- ZIO.service[ServerConfig]
    _            <- ZIO.logInfo(s"Starting server... http://localhost:${serverConfig.port}")

    _ <- server.provideSomeLayer(Server.defaultWith(config => config.binding("0.0.0.0", serverConfig.port)))
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

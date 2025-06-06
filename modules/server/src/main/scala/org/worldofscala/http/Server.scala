package org.worldofscala.http

import zio.*
import zio.http.*

import sttp.tapir.*
import sttp.tapir.files.*
import sttp.tapir.server.ziohttp.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.interceptor.cors.CORSInterceptor

import org.worldofscala.observability.*
import org.worldofscala.config.ServerConfig
import org.worldofscala.user.*

import org.worldofscala.organisation.*
import org.worldofscala.earth.*

import org.worldofscala.auth.JWTServiceLive
import org.worldofscala.Repository

object Server {

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

  def start: RIO[ServerConfig, Unit] = for {
    serverConfig <- ZIO.service[ServerConfig]
    _            <- ZIO.logInfo(s"Starting server... http://localhost:${serverConfig.port}")
    apiEndpoints <- org.worldofscala.http.HttpApi.endpoints
                      .provide(
                        // Service layers
                        UserServiceLive.layer,
                        OrganisationServiceLive.layer,
                        MeshServiceLive.layer,
                        JWTServiceLive.configuredLayer,
                        // Repository layers
                        UserRepositoryLive.layer,
                        OrganisationRepositoryLive.layer,
                        MeshRepositoryLive.layer,
                        Repository.dataLayer
                        // , ZLayer.Debug.mermaid
                      )

    docEndpoints = SwaggerInterpreter()
                     .fromServerEndpoints(apiEndpoints, "World of scala", "1.0.0")
    serverLayer = zio.http.Server.defaultWith(config => config.binding("0.0.0.0", serverConfig.port))
    _ <- zio.http.Server
           .serve(
             Routes(
               Method.GET / Root -> handler(Response.redirect(url"public/index.html"))
             ) ++
               ZioHttpInterpreter(serverOptions)
                 .toHttp(metricsEndpoint :: webJarRoutes :: apiEndpoints ::: docEndpoints)
           )
           .provideSomeLayer(serverLayer) <* Console.printLine("Server started !")
  } yield ()
}

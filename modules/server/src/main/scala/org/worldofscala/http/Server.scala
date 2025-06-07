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
import io.getquill.jdbczio.Quill.Postgres
import io.getquill.SnakeCase
import org.worldofscala.repository.Repository

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

  private def build = for {
    serverConfig <- ZIO.service[ServerConfig]
    _            <- ZIO.logInfo(s"Starting server... http://localhost:${serverConfig.port}")
    apiEndpoints <- HttpApi.endpoints

    docEndpoints = SwaggerInterpreter()
                     .fromServerEndpoints(apiEndpoints, "World of scala", "1.0.0")
    serverLayer = zio.http.Server.defaultWith(config => config.binding("0.0.0.0", serverConfig.port))
    _          <- zio.http.Server
           .serve(
             Routes(
               Method.GET / Root -> handler(Response.redirect(url"public/index.html"))
             ) ++
               ZioHttpInterpreter(serverOptions)
                 .toHttp(metricsEndpoint :: webJarRoutes :: apiEndpoints ::: docEndpoints)
           )
           .provideSomeLayer(serverLayer) <* Console.printLine("Server started !")
  } yield ()

  def start: ZIO[ServerConfig, Throwable, Unit] = build
    .provideSomeLayer(Repository.dataLayer)
}

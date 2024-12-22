package org.worldofscala.config

import zio.Config
import zio.Duration
import zio.config.magnolia.deriveConfig
import zio.ZLayer

final case class ServerConfig(port: Int)

object ServerConfig:
  given Config[ServerConfig] = deriveConfig[ServerConfig]

  def layer: ZLayer[Any, Config.Error, ServerConfig] = Configs.makeConfigLayer[ServerConfig]("server")

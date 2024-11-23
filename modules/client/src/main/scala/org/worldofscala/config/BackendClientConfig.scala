package org.worldofscala.config

import sttp.model.Uri

final case class BackendClientConfig(
  baseUrl: Option[Uri]
)

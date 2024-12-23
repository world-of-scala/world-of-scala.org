package org.worldofscala.http.endpoints

import sttp.tapir.*

trait HealthEndpoint {
  val healthEndpoint = endpoint
    .tag("health")
    .name("health")
    .get
    .in("health")
    .out(stringBody)
    .description("Health check")

}

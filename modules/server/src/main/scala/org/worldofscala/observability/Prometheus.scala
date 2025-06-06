package org.worldofscala.observability

import zio.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import io.prometheus.metrics.model.registry.PrometheusRegistry

val metricsRegistry: PrometheusRegistry = new PrometheusRegistry()

private val metrics: PrometheusMetrics[Task]   = PrometheusMetrics.default[Task](registry = metricsRegistry)
def metricsInterceptor                         = metrics.metricsInterceptor()
val metricsEndpoint: ZServerEndpoint[Any, Any] = metrics.metricsEndpoint

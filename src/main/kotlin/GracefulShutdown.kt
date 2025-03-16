package dev.hayden

import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.log
import io.ktor.server.response.header
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

val GracefulShutdown = createApplicationPlugin(
    name = "GracefulShutdown",
    createConfiguration = ::GracefulShutdownConfig
) {

    if (application.developmentMode && !pluginConfig.enableInDevelopmentMode) {
        application.log.trace("GracefulShutdown is not staring in development mode")
        return@createApplicationPlugin
    }

    val isShuttingDown = AtomicBoolean(false)
    val activeRequests = AtomicInteger(0)

    onCall { call ->
        activeRequests.incrementAndGet()
        if (isShuttingDown.get() && pluginConfig.enableCloseConnectionHeader) {
            call.response.header(HttpHeaders.Connection, "close")
        }
    }

    onCallRespond { _ ->
        activeRequests.decrementAndGet()
    }

    on(MonitoringEvent(ApplicationStopPreparing)) {
        runBlocking {
            isShuttingDown.set(true)
            withTimeout(pluginConfig.shutdownTimeout) {
                if (activeRequests.get() > 0) {
                    application.log.info("Requests still active. Waiting to gracefully shutdown...")
                }
                while (activeRequests.get() > 0) {
                    delay(100)
                }
            }
        }
    }
}

class GracefulShutdownConfig {
    var shutdownTimeout = 30.seconds
    var enableInDevelopmentMode = false
    var enableCloseConnectionHeader = true
}

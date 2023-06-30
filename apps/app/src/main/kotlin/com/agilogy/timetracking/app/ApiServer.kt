package com.agilogy.timetracking.app

import arrow.continuations.ktor.server
import com.agilogy.heroku.postgres.loadHerokuPostgresConfig
import com.agilogy.timetracking.driveradapters.httpapi.TimeTrackingApi
import io.ktor.server.netty.Netty
import kotlinx.coroutines.awaitCancellation
import kotlin.time.Duration.Companion.seconds

fun main() = app {
    val port = System.getenv("PORT")?.let { it.toInt() } ?: 8080
    val (jdbcUrl, username, password) = loadHerokuPostgresConfig()

    val timeTrackingApi = TimeTrackingApi(timeTrackingApp(jdbcUrl, username, password))

    server(Netty, port = port, host = "0.0.0.0", preWait = 5.seconds) {
        timeTrackingApi.routes()
    }
    awaitCancellation()
}

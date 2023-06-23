package com.agilogy.timetracking.app

import arrow.continuations.ktor.server
import com.agilogy.timetracking.driveradapters.httpapi.TimeTrackingApi
import io.ktor.server.netty.Netty
import kotlinx.coroutines.awaitCancellation
import java.net.URI
import kotlin.time.Duration.Companion.seconds

fun main() = app {
    val dbUri = URI(System.getenv("DATABASE_URL"))
    val port = System.getenv("PORT")?.let { it.toInt() } ?: 8080

    val (username, password) = dbUri.userInfo.split(":")
    val jdbcUrl = "jdbc:postgresql://" + dbUri.host + ':' + dbUri.port + dbUri.path

    val timeTrackingApi = TimeTrackingApi(timeTrackingApp(jdbcUrl, username, password))

    server(Netty, port = port, host = "0.0.0.0", preWait = 5.seconds) {
        timeTrackingApi.routes()
    }
    awaitCancellation()
}

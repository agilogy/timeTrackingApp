package com.agilogy.timetracking.app

import arrow.continuations.ktor.server
import com.agilogy.timetracking.domain.TimeTrackingApp
import com.agilogy.timetracking.driveradapters.httpapi.TimeTrackingApi
import io.ktor.server.netty.Netty
import kotlinx.coroutines.awaitCancellation
import kotlin.time.Duration.Companion.seconds

fun startApiServer(timeTrackingApp: TimeTrackingApp) = app {
    val port = System.getenv("PORT")?.let { it.toInt() } ?: 8080

    val timeTrackingApi = TimeTrackingApi(timeTrackingApp)
    server(Netty, port = port, host = "0.0.0.0", preWait = 5.seconds) {
        timeTrackingApi.routes()
    }
    awaitCancellation()
}

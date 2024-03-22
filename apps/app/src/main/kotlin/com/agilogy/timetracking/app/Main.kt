package com.agilogy.timetracking.app

import com.agilogy.heroku.postgres.loadHerokuPostgresConfig
import com.agilogy.timetracking.driveradapters.httpapi.TimeTrackingApi

fun main() = app {

    val (jdbcUrl, username, password) = loadHerokuPostgresConfig()

    val timeTrackingApp = timeTrackingApp(jdbcUrl, username, password)

    startBot(timeTrackingApp)
    startApiServer(timeTrackingApp)
}
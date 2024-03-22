package com.agilogy.timetracking.app

import com.agilogy.heroku.postgres.loadHerokuPostgresConfig

fun main() = app {
    val (jdbcUrl, username, password) = loadHerokuPostgresConfig()

    val timeTrackingApp = timeTrackingApp(jdbcUrl, username, password)

    startBot(timeTrackingApp)
    startApiServer(timeTrackingApp)
}

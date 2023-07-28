package com.agilogy.timetracking.migrationsapp

import arrow.continuations.SuspendApp
import com.agilogy.db.hikari.HikariCp
import com.agilogy.heroku.postgres.loadHerokuPostgresConfig
import com.agilogy.timetracking.migrations.runMigrations

fun main() = SuspendApp {
    val (jdbcUrl, username, password) = loadHerokuPostgresConfig()
    val dataSource = HikariCp.dataSource(jdbcUrl, username, password)
    dataSource.use { runMigrations(it) }
}

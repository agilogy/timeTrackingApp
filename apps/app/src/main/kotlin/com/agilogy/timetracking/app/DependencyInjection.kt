package com.agilogy.timetracking.app

import arrow.fx.coroutines.ResourceScope
import com.agilogy.db.hikari.HikariCp
import com.agilogy.timetracking.domain.TimeTrackingAppPrd
import com.agilogy.timetracking.drivenadapters.postgresdb.PostgresTimeEntriesRepository

suspend fun ResourceScope.timeTrackingApp(jdbcUrl: String, username: String, password: String): TimeTrackingAppPrd {
    val dataSource = HikariCp.dataSource(jdbcUrl, username, password).bind()
    val repo = PostgresTimeEntriesRepository(dataSource)
    return TimeTrackingAppPrd(repo)
}

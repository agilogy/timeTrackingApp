package com.agilogy.timetracking.app

import arrow.fx.coroutines.ResourceScope
import com.agilogy.db.hikari.HikariCp
import com.agilogy.timetracking.domain.TimeTrackingAppPrd
import com.agilogy.timetracking.drivenadapters.postgresdb.PostgresTimeEntriesRepository
import java.net.URI

suspend fun ResourceScope.timeTrackingApp(): TimeTrackingAppPrd {
    val dbUri = URI(System.getenv("DATABASE_URL"))

    val (username, password) = dbUri.userInfo.split(":")
    val jdbcUrl = "jdbc:postgresql://" + dbUri.host + ':' + dbUri.port + dbUri.path

    val dataSource = HikariCp.dataSource(jdbcUrl, username, password).bind()
    val repo = PostgresTimeEntriesRepository(dataSource)
    return TimeTrackingAppPrd(repo)
}

package com.agilogy.timetracking.drivenadapters.postgresdb

import com.agilogy.db.sql.ResultSetView
import com.agilogy.db.sql.Sql
import com.agilogy.db.sql.Sql.sql
import com.agilogy.db.sql.SqlParameter
import com.agilogy.db.sql.param
import com.agilogy.timetracking.domain.*
import java.time.Instant
import java.time.ZoneId
import javax.sql.DataSource
import kotlin.math.ceil

class PostgresTimeEntriesTestRepository(private val dataSource: DataSource) {
    private val DeveloperName.param: SqlParameter get() = name.param
    private val ProjectName.param: SqlParameter get() = name.param

    private fun ResultSetView.developer(columnIndex: Int): DeveloperName? = string(columnIndex)?.let {
        DeveloperName(it)
    }

    private fun ResultSetView.project(columnIndex: Int): ProjectName? = string(columnIndex)?.let { ProjectName(it) }
    suspend fun insertAll(timeEntries: List<TimeEntry>) = dataSource.sql {
        val sql = """insert into time_entries(developer, project, start, "end", zone_id) values (?, ?, ?, ?, ?)"""
        Sql.batchUpdate(sql) {
            timeEntries.forEach {
                addBatch(
                        it.developer.param,
                        it.project.param,
                        it.range.start.param,
                        it.range.endInclusive.param,
                        it.zoneId.id.param,
                )
            }
        }
        Unit
    }

    suspend fun selectAll(): List<TimeEntry> =
            dataSource.sql {
                val sql = """select developer, project, start, "end", zone_id from time_entries 
            """.trimMargin()
                Sql.select(sql) {
                    TimeEntry(
                            it.developer(1)!!,
                            it.project(2)!!,
                            it.timestamp(3)!!..it.timestamp(4)!!,
                            ZoneId.of(it.string(5)!!),
                    )
                }
            }
}
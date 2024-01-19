package com.agilogy.timetracking.drivenadapters.postgresdb

import com.agilogy.db.hikari.HikariCp
import com.agilogy.db.postgresql.PostgreSql
import com.agilogy.db.sql.Sql
import com.agilogy.db.sql.Sql.sql
import com.agilogy.timetracking.domain.DeveloperName
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.ProjectName
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import com.agilogy.timetracking.domain.test.InMemoryTimeEntriesRepository
import com.agilogy.timetracking.migrations.runMigrations
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.postgresql.util.PSQLException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset
import javax.sql.DataSource

class TimeEntriesRepositoryTest : FunSpec() {

    private suspend fun <A> withTestDataSource(database: String? = "test", f: suspend (DataSource) -> A) =
        HikariCp.dataSource(
            "jdbc:postgresql://localhost:5432/${database ?: ""}",
            "postgres",
            "postgres",
        ).use { dataSource ->
            f(dataSource)
        }

    private suspend fun <A> withPostgresTestRepo(f: suspend (TimeEntriesRepository) -> A) {
        withTestDataSource(null) { dataSource ->
            kotlin.runCatching { dataSource.sql { Sql.update("create database test") } }
                .recoverIf(Unit) { it is PSQLException && it.sqlState == PostgreSql.DuplicateDatabase }.getOrThrow()
        }

        withTestDataSource { dataSource ->
            runMigrations(dataSource, clean = true)
            f(PostgresTimeEntriesRepository(dataSource))
        }
    }

    private suspend fun <A> withInMemoryTestRepo(f: suspend (TimeEntriesRepository) -> A) =
        f(InMemoryTimeEntriesRepository())

    private fun <A> Result<A>.recoverIf(value: A, predicate: (Throwable) -> Boolean): Result<A> =
        this.recoverCatching { if (predicate(it)) value else throw it }

    private fun LocalDateTime.toLocalInstant() = atZone(ZoneOffset.systemDefault()).toInstant()
    private fun LocalDate.toLocalInstant() = atTime(0, 0).toLocalInstant()
    val today = LocalDate.of(2023, Month.APRIL, 1)
    fun at(hour: Int, minute: Int = 0) = today.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant()
    private fun date(day: Int): LocalDate = LocalDate.of(2023, Month.APRIL, day)
    private fun timePeriod(day: Int, hourFrom: Int, hours: Int): ClosedRange<Instant> {
        val from = date(day).atTime(LocalTime.of(hourFrom, 0)).toLocalInstant()
        return (from..from.plusSeconds(3600L * hours))
    }

    init {

        // TODO: Migrate tests using now and start to use at(hour, minute) instead
        val now = Instant.now()
        val hours = 1
        val start = now.minusSeconds(hours * 3600L)
        val zoneId: ZoneId = ZoneId.of("Australia/Sydney")

        val developer = DeveloperName("John")
        val project = ProjectName("Acme Inc.")

        val d1 = DeveloperName("d1")
        val d2 = DeveloperName("d2")
        val p = ProjectName("p")
        val p2 = ProjectName("p2")

        fun test(name: String, test: suspend TestScope.(TimeEntriesRepository) -> Unit) {
            context(name) {
                this.test("Postgres") {
                    withPostgresTestRepo { test(it) }
                }
                this.test("In Memory") {
                    withInMemoryTestRepo { test(it) }
                }
            }
        }

        @Suppress("UNUSED_PARAMETER")
        fun xtest(name: String, test: suspend TestScope.(TimeEntriesRepository) -> Unit) {
            super.xtest(name) {}
        }

        test("getHoursByDeveloperAndProject") { repo ->
            val testDay = date(1)
            repo.saveTimeEntries(
                listOf(
                    TimeEntry(d1, p, timePeriod(1, 9, 4), zoneId),
                    TimeEntry(d1, p, timePeriod(1, 14, 3), zoneId),
                    TimeEntry(d2, p, timePeriod(1, 10, 3), zoneId),
                ),
            )
            assertEquals(
                mapOf(
                    Pair(d1, p) to Hours(7),
                    Pair(d2, p) to Hours(3),
                ),
                repo.getHoursByDeveloperAndProject(testDay.toLocalInstant()..testDay.plusDays(1).toLocalInstant()),
            )
        }

        test("Get hours per developer") { repo ->
            repo.saveTimeEntries(listOf(TimeEntry(developer, project, start..now, zoneId)))
            val result = repo.getHoursByDeveloperAndProject(start..now)
            val expected = mapOf((developer to project) to Hours(hours))
            assertEquals(expected, result)
        }

        test("Get hours per developer when range is bigger than the developer hours") { repo ->
            repo.saveTimeEntries(listOf(TimeEntry(developer, project, start..now, zoneId)))
            val result = repo.getHoursByDeveloperAndProject(start.minusSeconds(7200L)..now.plusSeconds(7200L))
            val expected = mapOf((developer to project) to Hours(1))
            assertEquals(expected, result)
        }

        test("Get hours per developer when range makes no sense") { repo ->
            repo.saveTimeEntries(listOf(TimeEntry(developer, project, start..now, zoneId)))
            val resultOutside = repo.getHoursByDeveloperAndProject(start.plusSeconds(7200L)..now.minusSeconds(7200L))
            val resultInside = repo.getHoursByDeveloperAndProject(start.plusSeconds(2700L)..now.minusSeconds(2700L))
            val expected = emptyMap<Pair<DeveloperName, ProjectName>, Hours>()
            assertEquals(expected, resultOutside)
            assertEquals(expected, resultInside)
        }

        test("getHoursByDeveloperAndProject returns the hours in the interval") { repo ->
            repo.saveTimeEntries(listOf(TimeEntry(developer, project, at(9)..at(13), zoneId)))
            val actual = repo.getHoursByDeveloperAndProject(at(10)..at(12))
            val expected = mapOf((developer to project) to Hours(2))
            assertEquals(expected, actual)
        }

        test("getHoursByDeveloperAndProject rounds properly up") { repo ->
            repo.saveTimeEntries(listOf(TimeEntry(developer, project, at(10)..at(10, 30), zoneId)))
            val result = repo.getHoursByDeveloperAndProject(at(10)..at(11))
            val expected = mapOf((developer to project) to Hours(1))
            assertEquals(expected, result)
        }

        test("Get hours per developer when range is outside the developer hours") { repo ->
            repo.saveTimeEntries(listOf(TimeEntry(developer, project, start..now, zoneId)))
            val resultLeft = repo.getHoursByDeveloperAndProject(start.minusSeconds(3600L)..now.minusSeconds(7200L))
            val resultRight = repo.getHoursByDeveloperAndProject(start.plusSeconds(7200L)..now.plusSeconds(3600L))
            val expected = emptyMap<Pair<DeveloperName, ProjectName>, Hours>()
            assertEquals(expected, resultLeft)
            assertEquals(expected, resultRight)
        }

        test("Get hours per developer when only one part of the range is inside") { repo ->
            repo.saveTimeEntries(listOf(TimeEntry(developer, project, start..now, zoneId)))
            val resultStartInsideEndOutside = repo.getHoursByDeveloperAndProject(
                start.plusSeconds(1600L)..now.plusSeconds(1600L),
            )
            val resultStartOutsideEndInside = repo.getHoursByDeveloperAndProject(
                start.minusSeconds(1600L)..now.minusSeconds(1600L),
            )
            val expected = mapOf((developer to project) to Hours(1))
            assertEquals(expected, resultStartInsideEndOutside)
            assertEquals(expected, resultStartOutsideEndInside)
        }

        test("getDeveloperHoursByProjectAndDate") { repo ->
            repo.saveTimeEntries(
                listOf(
                    TimeEntry(d1, p, timePeriod(1, 9, 1), zoneId),
                    TimeEntry(d1, p, timePeriod(1, 11, 2), zoneId),
                    TimeEntry(d1, p2, timePeriod(1, 14, 4), zoneId),
                    TimeEntry(d1, p, timePeriod(2, 8, 6), zoneId),
                ),
            )

            assertEquals(
                listOf(
                    Triple(date(1), p, Hours(3)),
                    Triple(date(1), p2, Hours(4)),
                    Triple(date(2), p, Hours(6)),
                ),
                repo.getDeveloperHoursByProjectAndDate(d1, date(1)..date(2)),
            )
        }
    }
}

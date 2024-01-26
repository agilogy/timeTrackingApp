package com.agilogy.timetracking.domain

import arrow.core.Tuple5
import com.agilogy.timetracking.domain.test.InMemoryTimeEntriesRepository
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class TimeTrackingAppTest : FunSpec() {
    init {
        val now = Instant.now()
        val hours = 1
        val start = now.minusSeconds(hours * 3600L)
        val developer = DeveloperName("John")
        val project = ProjectName("Acme Inc.")
        val zoneId: ZoneId = ZoneId.of("Australia/Sydney")

        test("Save time entries") {
            // ARRANGE
            val timeEntriesRepository = InMemoryTimeEntriesRepository()
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val developerTimeEntries = listOf(Triple(project, start..now, zoneId))

            // ACT
            app.saveTimeEntries(developer, developerTimeEntries)

            // ASSERT
            val expected = listOf(TimeEntry(developer, project, start..now, zoneId))
            assertEquals(expected, timeEntriesRepository.getState())
        }

        test("Get hours per developer") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                listOf(
                    TimeEntry(
                        developer,
                        project,
                        start..now,
                        zoneId,
                    ),
                ),
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val result = app.getDeveloperHours(start..now)
            val expected = mapOf((developer to project) to Hours(hours))
            assertEquals(expected, result)
        }

        test("Get hours per developer ignores entries outside the time range") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                listOf(
                    TimeEntry(
                        developer,
                        project,
                        start..now,
                        zoneId,
                    ),
                    TimeEntry(
                        developer,
                        project,
                        start.minusSeconds(3600 * 24)..now.minusSeconds(3600 * 24),
                        zoneId,
                    ),
                ),
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val result = app.getDeveloperHours(start..now)
            val expected = mapOf((developer to project) to Hours(hours))
            assertEquals(expected, result)
        }

        test("Get developer hours by project and date") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                listOf(
                    TimeEntry(
                        developer,
                        project,
                        start..now,
                        zoneId,
                    ),
                ),
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            var range = LocalDate.now()..LocalDate.now()

            val result = app.getDeveloperHoursByProjectAndDate(developer, range)

            val expected = listOf(Triple(LocalDate.now(), project, Hours(hours)))
            assertEquals(expected, result)
        }

        test("Get a list of time entries") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                listOf(
                    TimeEntry(
                        developer,
                        project,
                        start..now,
                        zoneId,
                    ),
                ),
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val range = LocalDate.now()..LocalDate.now()

            val result = app.listTimeEntries(range, developer)

            val expected = listOf(
                Tuple5(developer, project, LocalDate.now(), start.localTime()..now.localTime(), zoneId),
            )
            assertEquals(expected, result)
        }

        test("Gets two time entries when a time entry starts and ends in different dates") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                listOf(
                    TimeEntry(
                        developer,
                        project,
                        start..now.plusSeconds(60 * 60 * 24),
                        zoneId,
                    ),
                ),
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val range = LocalDate.now()..LocalDate.now().plusDays(1)

            val result = app.listTimeEntries(range, developer)

            val expected = listOf(
                Tuple5(developer, project, LocalDate.now(), start.localTime()..LocalTime.of(23, 59, 59), zoneId),
                Tuple5(developer, project, LocalDate.now().plusDays(1), LocalTime.of(0, 0)..now.localTime(), zoneId),
            )
            assertEquals(expected, result)
        }

        test("Does not include time entries that don´t end within the date range") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                listOf(
                    TimeEntry(
                        developer,
                        project,
                        start..now.plusSeconds(60 * 60 * 24),
                        zoneId,
                    ),
                ),
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val range = LocalDate.now()..LocalDate.now()

            val result = app.listTimeEntries(range, developer)

            assert(result.isEmpty())
        }

        test("Does not include time entries that don´t start within the date range") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                listOf(
                    TimeEntry(
                        developer,
                        project,
                        start.minusSeconds(60 * 60 * 24)..now,
                        zoneId,
                    ),
                ),
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val range = LocalDate.now()..LocalDate.now()

            val result = app.listTimeEntries(range, developer)

            assert(result.isEmpty())
        }
    }
}

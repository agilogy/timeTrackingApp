package com.agilogy.timetracking.domain

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
        val developer2 = DeveloperName("Mariah")
        val project = ProjectName("Acme Inc.")
        val zoneId: ZoneId = ZoneId.of("Australia/Sydney")

        test("Save time entries") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository()
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val developerTimeEntries = listOf(Triple(project, start..now, zoneId))
            app.saveTimeEntries(developer, developerTimeEntries)
            val expected = listOf(TimeEntry(developer, project, start..now))
            assertEquals(expected, timeEntriesRepository.getState())
        }

        test("Get hours per developer") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(
                listOf(TimeEntry(developer, project, start..now)),
            )
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val result = app.getDeveloperHours(start..now)
            val expected = mapOf((developer to project) to Hours(hours))
            assertEquals(expected, result)
        }

        test("List my time entries returns no time entries when there are none") {
            // Arrange (initial state)
            val initialTimeEntries = emptyList<TimeEntry>()
            val timeEntriesRepository = InMemoryTimeEntriesRepository(initialTimeEntries)
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val range = instant(4, 1)..instant(5, 1)
            // Act
            val result = app.listTimeEntries(range, developer)
            // Assert result
            val expected = emptyList<TimeEntry>()
            assertEquals(expected, result)
            // Assert final state
            assertEquals(initialTimeEntries, timeEntriesRepository.getState())
        }

        test("List my time entries returns the user time entries") {
            // Arrange (initial state)
            val zone = ZoneId.of("Europe/Madrid")
            val date = LocalDate.of(2024, 4, 1)
            val startTime = LocalTime.of(9, 30)
            val endTime = LocalTime.of(11, 30)
            val timeEntry1 = TimeEntry(
                developer,
                project,
                date.atTime(startTime).atZone(zone).toInstant()..date.atTime(endTime).atZone(zone).toInstant(),
            )
            val timeEntry2 = TimeEntry(
                developer2,
                project,
                date.atTime(startTime).atZone(zone).toInstant()..date.atTime(endTime).atZone(zone).toInstant(),
            )
            val initialTimeEntries = listOf(timeEntry1, timeEntry2)
            val timeEntriesRepository = InMemoryTimeEntriesRepository(initialTimeEntries)
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val range = instant(4, 1)..instant(4, 30)
            // Act
            val result = app.listTimeEntries(range, developer)
            // Assert result
            val expected = listOf(timeEntry1)
            assertEquals(expected, result)
            // Assert final state
            assertEquals(initialTimeEntries, timeEntriesRepository.getState())
        }

        test("foo") {
            // val monthArb = Arb.bind(Arb.int(2023..2024), Arb.int(1..12)) { year, month -> YearMonth.of(year, month) }
        }
    }
}

private fun instant(month: Int, day: Int): Instant =
    LocalDate.of(2024, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant()

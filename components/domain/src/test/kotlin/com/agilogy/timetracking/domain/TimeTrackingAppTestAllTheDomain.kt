package com.agilogy.timetracking.domain

import arrow.core.Tuple5
import com.agilogy.timetracking.domain.test.InMemoryTimeEntriesRepository
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TimeTrackingAppTestAllTheDomain: FunSpec()  {
    init {
        /*
        val hours = 1
        val start = now.minusSeconds(hours * 3600L)
        val developer = DeveloperName("John")
        val project = ProjectName("Acme Inc.")
        val zoneId: ZoneId = ZoneId.of("Australia/Sydney")

         */

        test("List time entries when there are multiple developers") {
            val selectedDeveloper = DeveloperName("developer1")
            val selectedProject = ProjectName("project1")
            val zoneId = ZoneId.systemDefault()
            val expectedLocalDate = LocalDate.of(2024, 1, 26)
            val start = expectedLocalDate.atTime(8, 0).atZone(zoneId).toInstant()
            val end = start.plusSeconds(3600)
            val includedRange = start..end
            val expectedRange = start.localTime() .. end.localTime()

            val queryRange = expectedLocalDate.minusDays(1)..expectedLocalDate.plusDays(1)
            val selectedEntries = listOf(
                    TimeEntry(
                            developer = selectedDeveloper,
                            project = selectedProject,
                            range = includedRange,
                            zoneId = zoneId
                    )
            )
            val otherDeveloperEntries = listOf(
                    TimeEntry(
                            developer = DeveloperName("developer2"),
                            project = ProjectName("project2"),
                            range = includedRange,
                            zoneId = zoneId
                    ),
                    TimeEntry(
                            developer = DeveloperName("developer3"),
                            project = ProjectName("project3"),
                            range = includedRange,
                            zoneId = zoneId
                    )
            )
            val initialState = selectedEntries + otherDeveloperEntries
            val expected = selectedEntries.map {
                Tuple5(selectedDeveloper, selectedProject, expectedLocalDate, expectedRange, zoneId)
            }
            val repository = InMemoryTimeEntriesRepository(initialState)

            val app = TimeTrackingAppPrd(repository)

            val result = app.listTimeEntries(queryRange, selectedDeveloper)
            val finalState = repository.getState()

            assertEquals(expected, result)
            assertEquals(initialState, finalState)

        }

        test("List time entries for a developer with multiple projects") {}

        test("List time entries for a dateRange that does not totally cover an entry range") {}

        test("List all ent")
    }
}
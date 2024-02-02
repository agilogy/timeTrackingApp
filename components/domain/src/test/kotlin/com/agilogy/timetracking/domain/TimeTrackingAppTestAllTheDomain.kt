package com.agilogy.timetracking.domain

import arrow.core.Tuple5
import com.agilogy.timetracking.domain.test.InMemoryTimeEntriesRepository
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import java.time.ZoneId

class TimeTrackingAppTestAllTheDomain: FunSpec()  {
    init {
        test("List time entries when there are multiple developers") {
            val selectedDeveloper = DeveloperName("developer1")
            val selectedProject = ProjectName("project1")
            val localDate = LocalDate.of(2024, 1, 26)
            val zoneId = ZoneId.systemDefault()
            val start = localDate.atTime(8, 0).atZone(zoneId).toInstant()
            val end = start.plusSeconds(3600)
            val includedRange = start..end
            val expectedRange = start.localTime(zoneId)..end.localTime(zoneId)

            val queryRange = localDate.minusDays(1)..localDate.plusDays(1)
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
            val repository = InMemoryTimeEntriesRepository(initialState)
            val app = TimeTrackingAppPrd(repository)

            val actualResult = app.listTimeEntries(queryRange, selectedDeveloper)
            val actualState = repository.getState()

            val expectedResult = listOf(
                Tuple5(
                    first = selectedDeveloper,
                    second = selectedProject,
                    third = localDate,
                    fourth = expectedRange,
                    fifth = zoneId,
                )
            )
            val expectedState = initialState
            assertEquals(expectedResult, actualResult)
            assertEquals(expectedState, actualState)

        }

        test("List time entries for a developer with multiple projects") {}

        test("List time entries for a dateRange that does not totally cover an entry range") {}

        test("List all ent")
    }
}

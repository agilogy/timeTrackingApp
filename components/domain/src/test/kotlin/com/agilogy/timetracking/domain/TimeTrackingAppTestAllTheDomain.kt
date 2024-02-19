package com.agilogy.timetracking.domain

import com.agilogy.timetracking.domain.test.InMemoryTimeEntriesRepository
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import java.time.ZoneId

class TimeTrackingAppTestAllTheDomain: FunSpec()  {
    init {
        test("List time entries when there are multiple developers") {
            val scenario = createLastTimeEntriesScenarioWhereThereAreMultipleDevelopers()
            val repository = InMemoryTimeEntriesRepository(scenario.initialState.timeEntries)
            val app = TimeTrackingAppPrd(repository)

            val actualResult = app.listTimeEntries(scenario.inputQueryRange, scenario.inputDeveloper)
            val actualState = repository.getState()

            assertEquals(scenario.expectedResult, actualResult)
            assertEquals(scenario.expectedState, actualState)
        }

        test("List time entries for a developer with multiple projects") {}

        test("List time entries for a dateRange that does not totally cover an entry range") {}

        test("List all ent")
    }

    // Test fixtures

    data class AppState(
        val timeEntries: List<TimeEntry>,
    )

    data class TestScenario<I>(
        val initialState: AppState,
        val input: I,
        val expectedResult: List<TimeTrackingApp.ListTimeEntriesResult>,
        val expectedState: AppState,
    )

    data class ListTimeEntriesTestScenario(
        val initialState: AppState,
        val inputQueryRange: ClosedRange<LocalDate>,
        val inputDeveloper: DeveloperName?,
        val expectedResult: List<TimeTrackingApp.ListTimeEntriesResult>,
        val expectedState: AppState,
    )

    private fun createLastTimeEntriesScenarioWhereThereAreMultipleDevelopers(): ListTimeEntriesTestScenario {
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

        val initialState = AppState(
            timeEntries = selectedEntries + otherDeveloperEntries
        )

        val expectedResult = listOf(
            TimeTrackingApp.ListTimeEntriesResult(
                developer = selectedDeveloper,
                projectName = selectedProject,
                date = localDate,
                range = expectedRange,
                zoneId = zoneId,
            )
        )
        return ListTimeEntriesTestScenario(
            initialState = initialState,
            inputQueryRange = queryRange,
            inputDeveloper = selectedDeveloper,
            expectedResult = expectedResult,
            expectedState = initialState,
        )
    }
}

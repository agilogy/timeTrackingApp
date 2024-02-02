package com.agilogy.timetracking.domain

import arrow.core.Either
import com.agilogy.timetracking.infrastructure.TimeTrackingAppState
import com.agilogy.timetracking.infrastructure.TimeTrackingFakeApp
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant
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
            val initialState = TimeTrackingAppState.empty()
            val timeEntriesToSave = listOf(Triple(project, start..now, zoneId))
            val (finalState, result) = TimeTrackingFakeApp.withTimeTrackingApp(initialState) { app -> app.saveTimeEntries(developer = developer, timeEntries = timeEntriesToSave) }
            val expectedFinalState = initialState.withTimeEntries(timeEntriesToSave.map { triple ->  TimeEntry(developer, triple.first, triple.second, triple.third)})
            assert(result.isRight())
            assert(finalState == expectedFinalState)
        }

        test("Get hours per developer") {
            val currentTimeEntries = listOf(TimeEntry(developer, project, start..now, zoneId))
            val initialState = TimeTrackingAppState.empty().withTimeEntries(currentTimeEntries)
            val (finalState, result) = TimeTrackingFakeApp.withTimeTrackingApp(initialState) { app -> app.getDeveloperHours(start..now) }
            assertEquals(result, Either.Right(mapOf(Pair(Pair(developer, project), Hours(1)))))
            assert(finalState == initialState)
        }

        // TODO: Test the other methods of the app

        // TODO: Specially test the logic in listTimeEntries
    }
}

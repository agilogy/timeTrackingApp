package com.agilogy.timetracking.domain

import arrow.core.Either
import arrow.core.Tuple5
import com.agilogy.timetracking.infrastructure.TimeTrackingAppState
import com.agilogy.timetracking.infrastructure.TimeTrackingFakeApp
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TimeTrackingAppTest : FunSpec() {
    init {
        val now = Instant.now()
        val hours = 1
        val start = now.minusSeconds(hours * 3600L)
        val developer = DeveloperName("John")
        val project = ProjectName("Acme Inc.")
        val ausZoneId: ZoneId = ZoneId.of("Australia/Sydney")
        val madZoneId: ZoneId = ZoneId.of("Europe/Paris")

        test("Save time entries") {
            val initialState = TimeTrackingAppState.empty()
            val timeEntriesToSave = listOf(Triple(project, start..now, ausZoneId))
            val (finalState, result) = TimeTrackingFakeApp.withTimeTrackingApp(initialState) { app ->
                app.saveTimeEntries(
                    developer = developer,
                    timeEntries = timeEntriesToSave,
                )
            }
            val expectedFinalState = initialState.withTimeEntries(
                timeEntriesToSave.map { triple -> TimeEntry(developer, triple.first, triple.second, triple.third) },
            )
            assert(result.isRight())
            assert(finalState == expectedFinalState)
        }

        test("Get hours per developer") {
            val currentTimeEntries = listOf(TimeEntry(developer, project, start..now, ausZoneId))
            val initialState = TimeTrackingAppState.empty().withTimeEntries(currentTimeEntries)
            val (finalState, result) = TimeTrackingFakeApp.withTimeTrackingApp(initialState) { app ->
                app.getDeveloperHours(
                    start..now,
                )
            }
            assertEquals(Either.Right(mapOf(Pair(Pair(developer, project), Hours(1)))), result)
            assert(finalState == initialState)
        }

        test("Get hours per developer, project and name") {
            val madNow = LocalDateTime.of(2024, 2, 1, 15, 0).atZone(madZoneId)
            val madStart = LocalDateTime.of(2024, 2, 1, 9, 0).atZone(madZoneId)
            val localNow = madNow.toLocalDate()
            val localStart = madStart.toLocalDate()
            val currentTimeEntries = listOf(
                TimeEntry(developer, project, madStart.toInstant()..madNow.toInstant(), ausZoneId),
            )
            val initialState = TimeTrackingAppState.empty().withTimeEntries(currentTimeEntries)
            val (finalState, result) = TimeTrackingFakeApp.withTimeTrackingApp(initialState) { app ->
                app.getDeveloperHoursByProjectAndDate(
                    developer,
                    localStart..localNow,
                )
            }
            assertEquals(Either.Right(listOf(Triple(localNow, project, Hours(6)))), result)
            assert(finalState == initialState)
        }

        test("List all time entries per developer") {
            val madNow = LocalDateTime.of(2024, 2, 1, 15, 0).atZone(madZoneId)
            val madStart = LocalDateTime.of(2024, 2, 1, 9, 0).atZone(madZoneId)
            val localNow = madNow.toLocalDate()
            val localStart = madStart.toLocalDate()
            val currentTimeEntries = listOf(
                TimeEntry(developer, project, madStart.toInstant()..madNow.toInstant(), madZoneId),
            )
            val initialState = TimeTrackingAppState.empty().withTimeEntries(currentTimeEntries)
            val (finalState, result) = TimeTrackingFakeApp.withTimeTrackingApp(initialState) { app ->
                app.listTimeEntries(
                    localStart..localNow,
                    developer,
                )
            }
            assertEquals(
                Either.Right(
                    listOf(
                        Tuple5(developer, project, localNow, madStart.toLocalTime()..madNow.toLocalTime(), madZoneId),
                    ),
                ),
                result,
            )
            assert(finalState == initialState)
        }
    }
}

package com.agilogy.timetracking.domain

import com.agilogy.timetracking.domain.test.InMemoryTimeEntriesRepository
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
            val timeEntriesRepository = InMemoryTimeEntriesRepository()
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val developerTimeEntries = listOf(Triple(project, start..now, zoneId))
            app.saveTimeEntries(developer, developerTimeEntries)
            val expected = listOf(TimeEntry(developer, project, start..now, zoneId))
            assertEquals(expected, timeEntriesRepository.getState())
        }

        test("Get hours per developer") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository(listOf(TimeEntry(
                developer,
                project,
                start..now,
                zoneId
            )))
            val app = TimeTrackingAppPrd(timeEntriesRepository)
            val result = app.getDeveloperHours(start..now)
            val expected = mapOf((developer to project) to Hours(hours))
            assertEquals(expected, result)
        }

        // TODO: Test the other methods of the app

        // TODO: Specially test the logic in listTimeEntries
    }
}

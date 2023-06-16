package com.agilogy.timetracking.toggl

import com.agilogy.timetracking.domain.ProjectName
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.ZoneId

class TogglTest : FunSpec() {
    init {
        test("parses a toggle csv") {
            this::class.java.getResourceAsStream("/togglTimeEntries.csv").use { inputStream ->
                val zoneId = ZoneId.of("Europe/Madrid")
                val result: List<Triple<ProjectName, ClosedRange<Instant>, ZoneId>> = readTogglCsv(
                    inputStream!!,
                    zoneId,
                )
                val expected: List<Triple<ProjectName, ClosedRange<Instant>, ZoneId>> = listOf(
                    Triple(
                        ProjectName("ProjectA"),
                        Instant.parse("2023-05-12T06:30:00Z")..Instant.parse("2023-05-12T14:30:00Z"),
                        zoneId,
                    ),
                    Triple(
                        ProjectName("ProjectB"),
                        Instant.parse("2023-05-15T06:30:00Z")..Instant.parse("2023-05-15T14:30:00Z"),
                        zoneId,
                    ),
                    Triple(
                        ProjectName("ProjectA"),
                        Instant.parse("2023-05-15T14:30:00Z")..Instant.parse("2023-05-15T16:30:00Z"),
                        zoneId,
                    ),
                    Triple(
                        ProjectName("ProjectB"),
                        Instant.parse("2023-05-16T06:30:00Z")..Instant.parse("2023-05-16T14:30:00Z"),
                        zoneId,
                    ),
                    Triple(
                        ProjectName("ProjectB"),
                        Instant.parse("2023-05-17T06:30:00Z")..Instant.parse("2023-05-17T14:30:00Z"),
                        zoneId,
                    ),
                )
                assertEquals(expected, result)
            }
        }
        test("throws TogglInvalidTimeEntry on invalid toggle csv") {
            this::class.java.getResourceAsStream("/InvalidtogglTimeEntries.csv").use { inputStream ->
                val zoneId = ZoneId.of("Europe/Madrid")
                assertThrows<TogglInvalidTimeEntry> {
                    readTogglCsv(inputStream!!, zoneId)
                }
            }
        }
    }
}

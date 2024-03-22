package com.agilogy.timetracking.domain

import arrow.core.Tuple5
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

// TODO: Redesign TimeTrackingAppPrd dates:
//  - Report parameters should use LocalDate, Month, etc. and a(n optional?) user timezone
//  - Report responses should also use LocalDate et al. in the specified timezone
//  - The LocalDate <-> Instant conversions should follow DRY as far as possible.
//     - Specially avoid having them in both the DB and Kotlin
// TODO: Authentication & authorization
class TimeTrackingAppPrd(private val timeEntriesRepository: TimeEntriesRepository) : TimeTrackingApp {

    // TODO: CRUD projects

    // TODO: CRUD developers

    // TODO: saveTimeEntries validation:
    //  - Does project exist?
    //  - Does developer exist?
    //  - Are the time entries not overlapping?
    //  - Are the time entries under the maximum duration (let's say 12h)?
    // TODO: saveTimeEntries normalization
    //  - Merge together consecutive time entries
    override suspend fun saveTimeEntries(
        developer: DeveloperName,
        timeEntries: List<Triple<ProjectName, ClosedRange<Instant>, ZoneId>>,
    ) {
        timeEntriesRepository.saveTimeEntries(timeEntries.map { TimeEntry(developer, it.first, it.second, it.third) })
    }

    // TODO: Redesign getDeveloperHours to take a ClosedRange<LocalDate> and a timezone
    // TODO: getDeveloperHours
    override suspend fun getDeveloperHours(range: ClosedRange<Instant>): Map<Pair<DeveloperName, ProjectName>, Hours> =
        timeEntriesRepository.getHoursByDeveloperAndProject(range)

    override suspend fun getDeveloperHoursByProjectAndDate(developer: DeveloperName, dateRange: ClosedRange<LocalDate>):
        List<Triple<LocalDate, ProjectName, Hours>> =
        timeEntriesRepository.getDeveloperHoursByProjectAndDate(developer, dateRange)

    override suspend fun listTimeEntries(dateRange: ClosedRange<LocalDate>, developer: DeveloperName?):
        List<Tuple5<DeveloperName, ProjectName, LocalDate, ClosedRange<LocalTime>, ZoneId>> {
        val timeEntries = timeEntriesRepository.listTimeEntries(dateRange.toInstantRange(), developer)
        return timeEntries.flatMap { timeEntry ->
            fun row(date: LocalDate, range: ClosedRange<LocalTime>, zoneId: ZoneId) =
                Tuple5(timeEntry.developer, timeEntry.project, date, range, zoneId)

            val res = if (timeEntry.range.endInclusive.localDate() != timeEntry.localDate) {
                listOf(
                    row(
                        timeEntry.localDate,
                        timeEntry.range.start.localTime()..LocalTime.of(23, 59, 59),
                        timeEntry.zoneId,
                    ),

                    row(
                        timeEntry.localDate.plusDays(1),
                        LocalTime.of(0, 0)..timeEntry.range.endInclusive.localTime(),
                        timeEntry.zoneId,
                    ),
                )
            } else {
                listOf(
                    row(
                        timeEntry.localDate,
                        timeEntry.range.start.localTime()..timeEntry.range.endInclusive.localTime(),
                        timeEntry.zoneId,
                    ),
                )
            }
            res
        }
    }

    override suspend fun listProjects(): List<ProjectName> = listOf(ProjectName("Agilogy school"))
}

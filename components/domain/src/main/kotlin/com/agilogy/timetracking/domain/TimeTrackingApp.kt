package com.agilogy.timetracking.domain

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

interface TimeTrackingApp {

    suspend fun saveTimeEntries(
        developer: DeveloperName,
        timeEntries: List<Triple<ProjectName, ClosedRange<Instant>, ZoneId>>,
    )
    suspend fun getDeveloperHours(range: ClosedRange<Instant>): Map<Pair<DeveloperName, ProjectName>, Hours>
    suspend fun getDeveloperHoursByProjectAndDate(developer: DeveloperName, dateRange: ClosedRange<LocalDate>):
        List<Triple<LocalDate, ProjectName, Hours>>

    data class ListTimeEntriesResult(
        val developer: DeveloperName,
        val projectName: ProjectName,
        val date: LocalDate,
        val range: ClosedRange<LocalTime>,
        val zoneId: ZoneId,
    )

    suspend fun listTimeEntries(dateRange: ClosedRange<LocalDate>, developer: DeveloperName?):
        List<ListTimeEntriesResult>
}

@JvmInline
value class Hours(val value: Int)

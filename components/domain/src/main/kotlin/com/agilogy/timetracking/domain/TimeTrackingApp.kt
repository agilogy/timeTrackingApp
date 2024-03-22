package com.agilogy.timetracking.domain

import arrow.core.Tuple5
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

    suspend fun listTimeEntries(dateRange: ClosedRange<LocalDate>, developer: DeveloperName?):
        List<Tuple5<DeveloperName, ProjectName, LocalDate, ClosedRange<LocalTime>, ZoneId>>

    suspend fun listProjects(): List<ProjectName>
}

@JvmInline
value class Hours(val value: Int)

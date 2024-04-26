package com.agilogy.timetracking.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface TimeTrackingApp {

    suspend fun saveTimeEntries(
        developer: DeveloperName,
        timeEntries: List<Triple<ProjectName, ClosedRange<Instant>, ZoneId>>,
    )
    suspend fun getDeveloperHours(range: ClosedRange<Instant>): Map<Pair<DeveloperName, ProjectName>, Hours>
    suspend fun getDeveloperHoursByProjectAndDate(developer: DeveloperName, dateRange: ClosedRange<LocalDate>):
        List<Triple<LocalDate, ProjectName, Hours>>

    suspend fun listTimeEntries(range: ClosedRange<Instant>, developer: DeveloperName?): List<TimeEntry>

    suspend fun listProjects(): List<ProjectName>
}

@JvmInline
value class Hours(val value: Int)

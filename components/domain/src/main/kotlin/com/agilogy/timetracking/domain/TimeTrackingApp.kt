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

    /**
     * - Returns only time entries by the given developer (if one is provided)
     * - Returns only time entries in the given range
     * - Returns time entries sorted by start time ascending
     */
    suspend fun listTimeEntries(range: ClosedRange<Instant>, developer: DeveloperName?): List<TimeEntry>

    suspend fun listProjects(): List<ProjectName>
}

@JvmInline
value class Hours(val value: Int)

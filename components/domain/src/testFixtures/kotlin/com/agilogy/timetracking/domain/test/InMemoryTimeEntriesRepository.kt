package com.agilogy.timetracking.domain.test

import com.agilogy.timetracking.domain.DeveloperName
import com.agilogy.timetracking.domain.Hours
import com.agilogy.timetracking.domain.ProjectName
import com.agilogy.timetracking.domain.TimeEntriesRepository
import com.agilogy.timetracking.domain.TimeEntry
import com.agilogy.timetracking.domain.intersection
import com.agilogy.timetracking.domain.sum
import com.agilogy.timetracking.domain.toInstantRange
import java.time.Instant
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.roundToInt

data class TimeEntriesState(val timeEntries: List<TimeEntry>) {
    companion object {
        fun empty() = TimeEntriesState(emptyList())
    }
    fun withTimeEntries(newTimeEntries: List<TimeEntry>): TimeEntriesState = copy(timeEntries = timeEntries + newTimeEntries)
    fun setTimeEntries(timeEntries: List<TimeEntry>): TimeEntriesState = copy(timeEntries = timeEntries)
}


class InMemoryTimeEntriesRepository(initialState: TimeEntriesState = TimeEntriesState.empty()) : TimeEntriesRepository {

    private val internalState = mutableListOf<TimeEntry>()

    init {
        internalState.addAll(initialState.timeEntries)
    }

    override suspend fun saveTimeEntries(timeEntries: List<TimeEntry>) {
        internalState.addAll(timeEntries)
    }

    override suspend fun getHoursByDeveloperAndProject(
        range: ClosedRange<Instant>,
    ): Map<Pair<DeveloperName, ProjectName>, Hours> =
        internalState.filterIsIn(range)
            .groupBy({ it.developer to it.project }) { it.duration }
            .mapValues { Hours(ceil(it.value.sum().inWholeSeconds / 3_600.0).toInt()) }

    private fun List<TimeEntry>.filterIsIn(range: ClosedRange<Instant>) = mapNotNull { timeEntry ->
        range.intersection(timeEntry.range)?.let { timeEntry.copy(range = it) }
    }

    override suspend fun getDeveloperHoursByProjectAndDate(
        developer: DeveloperName,
        dateRange: ClosedRange<LocalDate>,
    ): List<Triple<LocalDate, ProjectName, Hours>> =
        internalState
            .filter { it.developer == developer }
            .filterIsIn(dateRange.toInstantRange())
            .groupBy({ it.localDate to it.project }) { it.duration }
            .mapValues { Hours(((it.value.sum().inWholeSeconds) / 3600.0).roundToInt()) }
            .map { (k, v) -> Triple(k.first, k.second, v) }

    override suspend fun listTimeEntries(timeRange: ClosedRange<Instant>, developer: DeveloperName?): List<TimeEntry> =
        internalState
            .filter { timeEntry -> developer?.let { it == timeEntry.developer } ?: true }
            .filterIsIn(timeRange)

    fun getState(): TimeEntriesState = TimeEntriesState(internalState)
}

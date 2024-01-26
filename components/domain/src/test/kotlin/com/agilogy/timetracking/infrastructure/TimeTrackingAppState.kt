package com.agilogy.timetracking.infrastructure

import arrow.core.Either
import com.agilogy.timetracking.domain.TimeEntry
import com.agilogy.timetracking.domain.TimeTrackingApp
import com.agilogy.timetracking.domain.TimeTrackingAppPrd
import com.agilogy.timetracking.domain.test.InMemoryTimeEntriesRepository
import com.agilogy.timetracking.domain.test.TimeEntriesState

data class TimeTrackingAppState(val timeEntriesState: TimeEntriesState) {
    companion object {
        fun empty() = TimeTrackingAppState(TimeEntriesState.empty())
    }

    fun withTimeEntries(newTimeEntries: List<TimeEntry>): TimeTrackingAppState = copy(timeEntriesState = timeEntriesState.withTimeEntries(newTimeEntries))
}

object TimeTrackingFakeApp{
    suspend fun <A>withTimeTrackingApp(state: TimeTrackingAppState, f: suspend (app: TimeTrackingApp) -> A ): Pair<TimeTrackingAppState, Either<Throwable, A>> {
        val timeEntriesRepository = InMemoryTimeEntriesRepository(state.timeEntriesState)
        val app = TimeTrackingAppPrd(timeEntriesRepository)
        val result =  Either.catch { f(app) }
        val finalState = TimeTrackingAppState(timeEntriesRepository.getState())
        return Pair(finalState, result)
    }

}
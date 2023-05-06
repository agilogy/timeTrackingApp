package com.agilogy.timetracking.driveradapters.httpapi

import arrow.core.raise.zipOrAccumulate
import com.agilogy.json.json
import com.agilogy.json.jsonObject
import com.agilogy.timetracking.domain.DeveloperName
import com.agilogy.timetracking.domain.TimeTrackingApp
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.time.LocalDate

class TimeTrackingApi(private val timeEntriesRegister: TimeTrackingApp) {

    context(Application)
    fun routes() = routing {
        get("/time-entries/daily-user-hours") {
            validated {
                zipOrAccumulate(
                    { requiredParam("userName") { DeveloperName(it) } },
                    { requiredParam("startDate") { LocalDate.parse(it) } },
                    { requiredParam("endDate") { LocalDate.parse(it) } },
                ) { userName, startDate, endDate ->
                    val result = timeEntriesRegister.getDeveloperHoursByProjectAndDate(userName, startDate..endDate)
                    val jsonResult = result.map { (date, project, hours) ->
                        jsonObject(
                            "date" to date.toString().json,
                            "project" to project.name.json,
                            "hours" to hours.value.json,
                        )
                    }.json
                    call.respondText(jsonResult.toString(), ContentType.Application.Json)
                }
            }
        }
    }
}

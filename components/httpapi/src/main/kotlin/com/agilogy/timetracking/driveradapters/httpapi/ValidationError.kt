package com.agilogy.timetracking.driveradapters.httpapi

import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.ensureNotNull
import arrow.core.raise.getOrElse
import com.agilogy.json.jsonObject
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.json.JsonPrimitive

data class ValidationError(val field: String, val description: String)

context(Raise<ValidationError>)
fun <A> PipelineContext<Unit, ApplicationCall>.requiredParam(
    param: String,
    parser: (String) -> A,
): A = catch({
    parser(ensureNotNull(call.request.queryParameters[param]) { ValidationError(param, "required") })
}) { raise(ValidationError(param, "invalid.format")) }

suspend fun PipelineContext<Unit, ApplicationCall>.validated(
    f: suspend Raise<NonEmptyList<ValidationError>>.() -> Unit,
): Unit =
    f.getOrElse { errors ->
        call.respondText(
            jsonObject(
                "validationErrors" to jsonObject(
                    errors.map {
                        it.field to JsonPrimitive(it.description)
                    },
                ),
            ).toString(),
            ContentType.Application.Json,
            HttpStatusCode.BadRequest,
        )
    }

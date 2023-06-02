package com.agilogy.timetracking.toggl

import com.agilogy.timetracking.domain.ProjectName
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import java.time.*
import java.time.temporal.ChronoUnit

fun readTogglCsv(inputStream: InputStream, zoneId: ZoneId): List<Triple<ProjectName, ClosedRange<Instant>, ZoneId>> =
    csvReader().readAllWithHeader(inputStream).map { row ->
        val projectName = ProjectName(row.getValue("Project"))
        val startDate = LocalDate.parse(row.getValue("Start date"))
        val startTime = LocalTime.parse(row.getValue("Start time"))
        val parsedDuration = LocalTime.parse(row.getValue("Duration"))

        val startInstant = LocalDateTime.of(startDate, startTime).atZone(zoneId).toInstant()

        val duration = Duration.of(parsedDuration.toSecondOfDay().toLong(), ChronoUnit.SECONDS)!!
        val range = startInstant .. startInstant.plus(duration)
        Triple(projectName, range, zoneId)
    }

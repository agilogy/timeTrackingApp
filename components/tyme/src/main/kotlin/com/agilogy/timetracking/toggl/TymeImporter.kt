package com.agilogy.timetracking.toggl

import com.agilogy.timetracking.domain.ProjectName
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import java.lang.Long.parseLong
import java.time.Instant
import java.time.ZoneId

fun readTymeCsv(inputStream: InputStream, zoneId: ZoneId): List<Triple<ProjectName, ClosedRange<Instant>, ZoneId>> =
    csvReader { delimiter = ';' }.readAllWithHeader(inputStream).map { row ->
        val projectName = ProjectName(row.getValue("project"))
        val unixStart = parseLong(row.getValue("unix_start"))
        val unixEnd = parseLong(row.getValue("unix_end"))
        val range = Instant.ofEpochSecond(unixStart)..Instant.ofEpochSecond(unixEnd)
        Triple(projectName, range, zoneId)
    }

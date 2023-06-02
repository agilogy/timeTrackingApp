package com.agilogy.timetracking.driveradapters.console

import arrow.core.raise.effect
import arrow.core.raise.fold
import com.agilogy.timetracking.domain.TimeTrackingApp
import com.agilogy.timetracking.domain.toInstantRange
import com.agilogy.timetracking.domain.toLocalDateRange
import java.util.regex.Pattern

class ConsoleAdapter(
    private val timeTrackingApp: TimeTrackingApp,
    private val console: Console = Console(),
) {

    suspend fun mainLoop() {
        print("timeTrackingApp >> ")
        var args = readln().splitPreservingQuotedStrings()

        while (args != listOf("exit")) {
            effect { runCommand(ArgsParser.parse(args)) }.fold(
                { println(it.message); runCommand(Help) },
                { println("Command executed successfully") },
            )
            print("timeTrackingApp >> ")
            args = readln().splitPreservingQuotedStrings()
        }
    }

    private fun String.splitPreservingQuotedStrings(): List<String> {
        val words = ArrayList<String>()
        val pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)")

        val matcher = pattern.matcher(this)
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Quoted string found, add it as a whole
                words.add(matcher.group(1))
            } else {
                // Non-quoted word found, split it using whitespaces
                val splitWords = matcher.group(2).split("\\s+").toTypedArray()
                for (splitWord in splitWords) {
                    if (splitWord.isNotEmpty()) {
                        words.add(splitWord)
                    }
                }
            }
        }

        return words
    }
    private suspend fun runCommand(cmd: Command) {
        when (cmd) {
            is GlobalReport -> {
                val report = timeTrackingApp.getDeveloperHours(cmd.yearMonth.toInstantRange())
                console.print(report)
            }

            is DeveloperReport -> {
                val report =
                    timeTrackingApp.getDeveloperHoursByProjectAndDate(cmd.developer, cmd.yearMonth.toLocalDateRange())
                console.print(report)
            }

            Help -> console.printHelp(ArgsParser.help())
            is ListTimeEntries ->
                console.printTimeEntries(
                    timeTrackingApp.listTimeEntries(cmd.yearMonth.toLocalDateRange(), cmd.developer),
                )

            is AddTimeEntry ->
                timeTrackingApp.saveTimeEntries(cmd.developer, listOf(Triple(cmd.project, cmd.range, cmd.zoneId)))
        }
    }
}

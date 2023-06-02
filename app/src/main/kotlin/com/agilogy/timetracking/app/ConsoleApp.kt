package com.agilogy.timetracking.app

import com.agilogy.timetracking.driveradapters.console.ConsoleAdapter

fun main(@Suppress("UNUSED_PARAMETER")a: Array<String>): Unit = app {
    ConsoleAdapter(timeTrackingApp()).mainLoop()
}

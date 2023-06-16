package com.agilogy.timetracking.toggl

class TogglInvalidTimeEntry(override val cause: Throwable) : Exception("Invalid Time Entry")

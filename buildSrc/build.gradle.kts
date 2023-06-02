repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

plugins {
    kotlin("jvm") version "1.8.21"
    //id("com.adarshr.test-logger") version ("3.2.0")
}

/*
configurations {
    testlogger {
        theme = ThemeType.STANDARD
        showExceptions = true
        showStackTraces = true
        showFullStackTraces = false
        showCauses = true
        slowThreshold = 2000
        showSummary = true
        showSimpleNames = false
        showPassed = true
        showSkipped = true
        showFailed = true
        showOnlySlow = false
        showStandardStreams = false
        showPassedStandardStreams = true
        showSkippedStandardStreams = true
        showFailedStandardStreams = true
        logLevel = LogLevel.LIFECYCLE
    }
}*/

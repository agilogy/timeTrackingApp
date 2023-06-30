package com.agilogy.timetracking.migrations

import arrow.continuations.SuspendApp
import com.agilogy.heroku.postgres.loadHerokuPostgresConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.slf4j.LoggerFactory

fun main() = SuspendApp {
    val (jdbcUrl, username, password) = loadHerokuPostgresConfig()

    val flywayConfig: FluentConfiguration = Flyway.configure()
        .dataSource(jdbcUrl, username, password)
        .group(true)
        .outOfOrder(false)
        .baselineOnMigrate(true)
        .locations("classpath:${this.javaClass.packageName}.scripts")
        .loggers("slf4j")

    val validated = flywayConfig
        .ignoreMigrationPatterns("*:pending")
        .load()
        .validateWithResult()

    if (!validated.validationSuccessful) {
        val logger = LoggerFactory.getLogger("RunMigrations")
        for (error in validated.invalidMigrations) {
            logger.warn(
                """
                        |Failed to validate migration:
                        |  - version: ${error.version}
                        |  - path: ${error.filepath}
                        |  - description: ${error.description}
                        |  - error code: ${error.errorDetails.errorCode}
                        |  - error message: ${error.errorDetails.errorMessage}
                    """.trimMargin("|").trim(),
            )
        }
    }
    flywayConfig.load().migrate()
}

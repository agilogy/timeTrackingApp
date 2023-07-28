package com.agilogy.timetracking.migrations

import com.agilogy.timetracking.migrations.scripts.Migration202306231200
import kotlinx.coroutines.runBlocking
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.migration.Context
import org.flywaydb.core.api.migration.JavaMigration
import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.sql.DataSource

abstract class KotlinMigration(private val version: String, private val title: String) : JavaMigration {

    override fun getVersion(): MigrationVersion = MigrationVersion.fromVersion(version)

    override fun getDescription(): String = title

    override fun getChecksum(): Int? = null

    override fun canExecuteInTransaction(): Boolean = true

    final override fun migrate(context: Context) = runBlocking {
        with(context.connection!!) {
            migrate()
        }
    }

    context(Connection)
    abstract suspend fun migrate()
}

fun runMigrations(dataSource: DataSource, clean: Boolean = false) {
    val flywayConfig: FluentConfiguration = Flyway.configure()
        .dataSource(dataSource)
        .group(true)
        .outOfOrder(false)
        .baselineOnMigrate(true)
        .locations("classpath:${Migration202306231200::class.java.packageName.replace('.', '/')}")
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
    if (clean) {
        flywayConfig.cleanDisabled(false).load().clean()
    }
    println(flywayConfig.load().migrate())
}

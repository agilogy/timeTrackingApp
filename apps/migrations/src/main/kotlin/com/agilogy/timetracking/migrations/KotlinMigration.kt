package com.agilogy.timetracking.migrations

import kotlinx.coroutines.runBlocking
import org.flywaydb.core.api.MigrationVersion
import org.flywaydb.core.api.migration.Context
import org.flywaydb.core.api.migration.JavaMigration
import java.sql.Connection

abstract class KotlinMigration(private val version: String, private val title: String) : JavaMigration {

    override fun getVersion(): MigrationVersion = MigrationVersion.fromVersion(version)

    override fun getDescription(): String = title

    override fun getChecksum(): Int? = null

    override fun canExecuteInTransaction(): Boolean = true

    final override fun migrate(context: Context) = runBlocking {
        context.connection!!.use {
            with(it) {
                migrate()
            }
        }
    }

    context(Connection)
    abstract suspend fun migrate()
}

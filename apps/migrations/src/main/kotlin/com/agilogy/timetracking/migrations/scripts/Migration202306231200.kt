package com.agilogy.timetracking.migrations.scripts

import com.agilogy.db.sql.Sql.update
import com.agilogy.timetracking.migrations.KotlinMigration
import java.sql.Connection

class Migration202306231200 : KotlinMigration("202306231200", "Initial migration") {

    context(Connection)
    override suspend fun migrate() {
        update(
            """
            |create table if not exists time_entries(
            |id serial,
            |developer text not null, 
            |project text not null, 
            |start timestamptz not null, 
            |"end" timestamptz not null,
            |zone_id text not null
            |)
            """,
        )
    }
}

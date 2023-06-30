package com.agilogy.timetracking.migrations.scripts

import com.agilogy.db.sql.Sql.update
import com.agilogy.timetracking.migrations.KotlinMigration
import java.sql.Connection

class Migration20230623 : KotlinMigration("20230623", "Initial migration") {

    context(Connection)
    override suspend fun migrate() {
        update(
            """
            |create table time_entries(
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

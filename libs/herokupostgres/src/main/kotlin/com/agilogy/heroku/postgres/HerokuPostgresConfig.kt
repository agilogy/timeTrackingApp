package com.agilogy.heroku.postgres

import java.net.URI

data class HerokuPostgresConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
) {
    override fun toString(): String =
        "HerokuPostgresConfig($jdbcUrl,$username,\"********\")"
}

fun loadHerokuPostgresConfig(): HerokuPostgresConfig {
    val dbUri = URI(System.getenv("DATABASE_URL"))
    val (username, password) = dbUri.userInfo.split(":")
    val jdbcUrl = "jdbc:postgresql://" + dbUri.host + ':' + dbUri.port + dbUri.path
    return HerokuPostgresConfig(jdbcUrl, username, password)
}

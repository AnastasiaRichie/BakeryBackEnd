package org.bakery_tm.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/bakery_test_db"
        username = "postgres"
        password = "postgres"
        driverClassName = "org.postgresql.Driver"
    }
    Database.connect(HikariDataSource(config))
}
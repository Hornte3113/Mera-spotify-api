package com.example

import com.example.repository.Artists
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

// Importa tus tablas aquí
// import com.example.Users

fun Application.configureDatabases() {
    // Leemos la configuración del archivo application.yaml o application.conf
    val config = environment.config.config("storage")
    val url = config.property("jdbcUrl").getString()
    val user = config.property("username").getString()
    val password = config.property("password").getString()

    val hikariConfig = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = url
        username = user
        this.password = password
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    val database = Database.connect(HikariDataSource(hikariConfig))

    transaction(database) {
        // SchemaUtils.create(Users)
        SchemaUtils.create(Artists)
    }
}
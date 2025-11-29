package com.example

// Asegúrate de importar los 3 objetos de repositorio
import com.example.repository.Artists
import com.example.repository.Albums
import com.example.repository.Tracks // <--- Nuevo Import

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
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
        // SchemaUtils.create(Users) // Si usas usuarios, descomenta esto

        // El orden importa para las Claves Foráneas:
        SchemaUtils.create(Artists) // 1. Primero Artistas
        SchemaUtils.create(Albums)  // 2. Luego Álbumes (depende de Artistas)
        SchemaUtils.create(Tracks)  // 3. Al final Tracks (depende de ambos)
    }
}
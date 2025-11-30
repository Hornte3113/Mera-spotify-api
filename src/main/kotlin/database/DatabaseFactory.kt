package com.example.database

import com.example.models.Albumes
import com.example.models.Artistas
import com.example.models.Tracks
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private val dotenv = dotenv {
        ignoreIfMissing = true // No falla si no existe el .env
    }
    
    fun init() {
        val database = Database.connect(createHikariDataSource())
        
        // Crear las tablas si no existen
        transaction(database) {
            SchemaUtils.create(Artistas, Albumes, Tracks)
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            
            // Opción 1: Variables individuales (DB_HOST, DB_USER, etc.)
            val dbHost = dotenv["DB_HOST"] ?: System.getenv("DB_HOST")
            val dbPort = dotenv["DB_PORT"] ?: System.getenv("DB_PORT")
            val dbName = dotenv["DB_NAME"] ?: System.getenv("DB_NAME")
            val dbUser = dotenv["DB_USER"] ?: System.getenv("DB_USER")
            val dbPassword = dotenv["DB_PASSWORD"] ?: System.getenv("DB_PASSWORD")
            
            // Opción 2: URL completa (DATABASE_URL)
            val databaseUrl = dotenv["DATABASE_URL"] ?: System.getenv("DATABASE_URL")
            
            // Prioridad: URL completa > Variables individuales > Default
            jdbcUrl = when {
                databaseUrl != null -> databaseUrl
                dbHost != null && dbUser != null -> 
                    "jdbc:postgresql://$dbHost:${dbPort ?: "5432"}/$dbName?user=$dbUser&password=$dbPassword"
                else -> "jdbc:postgresql://localhost:5432/musica_db?user=postgres&password=postgres"
            }
            
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

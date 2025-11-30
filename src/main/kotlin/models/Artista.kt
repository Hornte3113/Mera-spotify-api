package com.example.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

// Tabla de la base de datos
object Artistas : UUIDTable("artistas") {
    val name = varchar("name", 100)
    val genre = varchar("genre", 50).nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
}

// Data class para respuestas JSON
data class Artista(
    val id: UUID,
    val name: String,
    val genre: String?,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

// Data class para crear/actualizar artistas
data class ArtistaInput(
    val name: String,
    val genre: String?
)

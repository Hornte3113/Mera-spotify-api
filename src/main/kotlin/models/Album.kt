package com.example.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

// Tabla de la base de datos
object Albumes : UUIDTable("albumes") {
    val title = varchar("title", 150)
    val releaseYear = integer("release_year")
    val artistId = reference("artist_id", Artistas)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
}

// Data class para respuestas JSON
data class Album(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val artistId: UUID,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

// Data class para crear/actualizar álbumes
data class AlbumInput(
    val title: String,
    val releaseYear: Int,
    val artistId: UUID
)

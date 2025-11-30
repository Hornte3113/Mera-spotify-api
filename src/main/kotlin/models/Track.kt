package com.example.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

// Tabla de la base de datos
object Tracks : UUIDTable("tracks") {
    val title = varchar("title", 150)
    val duration = integer("duration")
    val albumId = reference("album_id", Albumes)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
}

// Data class para respuestas JSON
data class Track(
    val id: UUID,
    val title: String,
    val duration: Int,
    val albumId: UUID,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

// Data class para crear/actualizar tracks
data class TrackInput(
    val title: String,
    val duration: Int,
    val albumId: UUID
)

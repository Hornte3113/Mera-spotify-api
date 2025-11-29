

package com.example.repository

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Tracks : Table("tracks") {
    val id = uuid("id")
    val name = varchar("name", 150)
    val duration = integer("duration") // Duración en segundos o milisegundos
    val previewUrl = text("preview_url").nullable() // La 'key' del archivo MP3 en S3

    // Relaciones
    val albumId = uuid("album_id").references(Albums.id, onDelete = ReferenceOption.CASCADE).nullable()
    val artistId = uuid("artist_id").references(Artists.id, onDelete = ReferenceOption.CASCADE)

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}
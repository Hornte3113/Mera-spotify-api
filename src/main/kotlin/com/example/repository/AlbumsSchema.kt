

package com.example.repository

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Albums : Table("albums") {
    val id = uuid("id")
    val name = varchar("name", 100)
    val year = integer("year")
    val albumArt = text("album_art") // URL de la portada en S3

    // Conexión: Este álbum pertenece a un artista.
    // onDelete = CASCADE significa que si borras al artista, se borran sus álbumes automáticamente.
    val artistId = uuid("artist_id").references(Artists.id, onDelete = ReferenceOption.CASCADE)

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}
package com.example.services

import com.example.database.DatabaseFactory.dbQuery
import com.example.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class AlbumService {
    
    suspend fun create(albumInput: AlbumInput): Album? = dbQuery {
        // Verificar que el artista existe
        val artistaExiste = Artistas.selectAll()
            .where { Artistas.id eq albumInput.artistId }
            .count() > 0
        
        if (!artistaExiste) {
            throw IllegalArgumentException("El artista con ID ${albumInput.artistId} no existe")
        }
        
        val insertStatement = Albumes.insert {
            it[title] = albumInput.title
            it[releaseYear] = albumInput.releaseYear
            it[artistId] = albumInput.artistId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToAlbum)
    }

    suspend fun read(id: UUID): Album? = dbQuery {
        Albumes.selectAll()
            .where { Albumes.id eq id }
            .map(::rowToAlbum)
            .singleOrNull()
    }

    suspend fun readAll(): List<Album> = dbQuery {
        Albumes.selectAll().map(::rowToAlbum)
    }

    suspend fun update(id: UUID, albumInput: AlbumInput): Boolean = dbQuery {
        // Verificar que el artista existe
        val artistaExiste = Artistas.selectAll()
            .where { Artistas.id eq albumInput.artistId }
            .count() > 0
        
        if (!artistaExiste) {
            throw IllegalArgumentException("El artista con ID ${albumInput.artistId} no existe")
        }
        
        Albumes.update({ Albumes.id eq id }) {
            it[title] = albumInput.title
            it[releaseYear] = albumInput.releaseYear
            it[artistId] = albumInput.artistId
        } > 0
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        // Verificar si tiene tracks antes de borrar
        val tieneTracks = Tracks.selectAll()
            .where { Tracks.albumId eq id }
            .count() > 0
        
        if (tieneTracks) {
            throw IllegalStateException("No se puede eliminar el álbum porque tiene tracks asociados")
        }
        
        Albumes.deleteWhere { Albumes.id eq id } > 0
    }

    private fun rowToAlbum(row: ResultRow) = Album(
        id = row[Albumes.id].value,
        title = row[Albumes.title],
        releaseYear = row[Albumes.releaseYear],
        artistId = row[Albumes.artistId].value,
        createdAt = row[Albumes.createdAt],
        updatedAt = row[Albumes.updatedAt]
    )
}

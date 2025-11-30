package com.example.services

import com.example.database.DatabaseFactory.dbQuery
import com.example.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class TrackService {
    
    suspend fun create(trackInput: TrackInput): Track? = dbQuery {
        // Verificar que el álbum existe
        val albumExiste = Albumes.selectAll()
            .where { Albumes.id eq trackInput.albumId }
            .count() > 0
        
        if (!albumExiste) {
            throw IllegalArgumentException("El álbum con ID ${trackInput.albumId} no existe")
        }
        
        val insertStatement = Tracks.insert {
            it[title] = trackInput.title
            it[duration] = trackInput.duration
            it[albumId] = trackInput.albumId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToTrack)
    }

    suspend fun read(id: UUID): Track? = dbQuery {
        Tracks.selectAll()
            .where { Tracks.id eq id }
            .map(::rowToTrack)
            .singleOrNull()
    }

    suspend fun readAll(): List<Track> = dbQuery {
        Tracks.selectAll().map(::rowToTrack)
    }

    suspend fun update(id: UUID, trackInput: TrackInput): Boolean = dbQuery {
        // Verificar que el álbum existe
        val albumExiste = Albumes.selectAll()
            .where { Albumes.id eq trackInput.albumId }
            .count() > 0
        
        if (!albumExiste) {
            throw IllegalArgumentException("El álbum con ID ${trackInput.albumId} no existe")
        }
        
        Tracks.update({ Tracks.id eq id }) {
            it[title] = trackInput.title
            it[duration] = trackInput.duration
            it[albumId] = trackInput.albumId
        } > 0
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        Tracks.deleteWhere { Tracks.id eq id } > 0
    }

    private fun rowToTrack(row: ResultRow) = Track(
        id = row[Tracks.id].value,
        title = row[Tracks.title],
        duration = row[Tracks.duration],
        albumId = row[Tracks.albumId].value,
        createdAt = row[Tracks.createdAt],
        updatedAt = row[Tracks.updatedAt]
    )
}

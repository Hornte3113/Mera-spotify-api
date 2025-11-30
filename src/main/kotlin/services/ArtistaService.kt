package com.example.services

import com.example.database.DatabaseFactory.dbQuery
import com.example.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class ArtistaService {
    
    suspend fun create(artistaInput: ArtistaInput): Artista? = dbQuery {
        val insertStatement = Artistas.insert {
            it[name] = artistaInput.name
            it[genre] = artistaInput.genre
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToArtista)
    }

    suspend fun read(id: UUID): Artista? = dbQuery {
        Artistas.selectAll()
            .where { Artistas.id eq id }
            .map(::rowToArtista)
            .singleOrNull()
    }

    suspend fun readAll(): List<Artista> = dbQuery {
        Artistas.selectAll().map(::rowToArtista)
    }

    suspend fun update(id: UUID, artistaInput: ArtistaInput): Boolean = dbQuery {
        Artistas.update({ Artistas.id eq id }) {
            it[name] = artistaInput.name
            it[genre] = artistaInput.genre
        } > 0
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        // Verificar si tiene álbumes antes de borrar
        val tieneAlbumes = Albumes.selectAll()
            .where { Albumes.artistId eq id }
            .count() > 0
        
        if (tieneAlbumes) {
            throw IllegalStateException("No se puede eliminar el artista porque tiene álbumes asociados")
        }
        
        Artistas.deleteWhere { Artistas.id eq id } > 0
    }

    private fun rowToArtista(row: ResultRow) = Artista(
        id = row[Artistas.id].value,
        name = row[Artistas.name],
        genre = row[Artistas.genre],
        createdAt = row[Artistas.createdAt],
        updatedAt = row[Artistas.updatedAt]
    )
}


package com.example.services

import com.example.models.Album
import com.example.repository.Albums
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class AlbumService(private val s3Service: S3Service) {

    // CREAR ÁLBUM
    suspend fun create(name: String, year: Int, artistId: UUID, imageBytes: ByteArray): Album {
        // 1. Subir portada a S3
        val imageKey = s3Service.uploadFile("album-$name.jpg", imageBytes, "image/jpeg")

        // 2. Guardar en BD vinculado al artista
        val newId = dbQuery {
            val id = UUID.randomUUID()
            Albums.insert {
                it[Albums.id] = id
                it[Albums.name] = name
                it[Albums.year] = year
                it[Albums.artistId] = artistId
                it[Albums.albumArt] = imageKey
            }
            id
        }
        // 3. Devolver con URL lista
        return Album(newId, name, year, s3Service.getPresignedUrl(imageKey), artistId)
    }

    // LISTAR TODOS
    suspend fun getAll(): List<Album> {
        return dbQuery {
            Albums.selectAll().map {
                Album(
                    it[Albums.id],
                    it[Albums.name],
                    it[Albums.year],
                    s3Service.getPresignedUrl(it[Albums.albumArt]),
                    it[Albums.artistId]
                )
            }
        }
    }

    // LISTAR POR ARTISTA (Para ver solo los álbumes de un artista)
    suspend fun getByArtist(artistId: UUID): List<Album> {
        return dbQuery {
            Albums.select { Albums.artistId eq artistId }.map {
                Album(
                    it[Albums.id],
                    it[Albums.name],
                    it[Albums.year],
                    s3Service.getPresignedUrl(it[Albums.albumArt]),
                    it[Albums.artistId]
                )
            }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
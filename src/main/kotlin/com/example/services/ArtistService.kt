package com.example.com.example.services



import com.example.com.example.models.Artist
import com.example.com.example.repository.Artists
// Asegúrate de importar tu S3Service correctamente
import com.example.com.example.services.S3Service
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class ArtistService(private val s3Service: S3Service) {

    suspend fun create(name: String, genre: String, imageBytes: ByteArray): Artist {
        // Subir a S3
        val imageKey = s3Service.uploadFile("artist-$name.jpg", imageBytes, "image/jpeg")

        // Guardar en BD
        val newId = dbQuery {
            val id = UUID.randomUUID()
            Artists.insert {
                it[Artists.id] = id
                it[Artists.name] = name
                it[Artists.genre] = genre
                it[Artists.image] = imageKey
            }
            id
        }
        return Artist(newId, name, genre, s3Service.getPresignedUrl(imageKey))
    }

    suspend fun getAll(): List<Artist> {
        return dbQuery {
            Artists.selectAll().map {
                Artist(
                    it[Artists.id],
                    it[Artists.name],
                    it[Artists.genre],
                    s3Service.getPresignedUrl(it[Artists.image])
                )
            }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
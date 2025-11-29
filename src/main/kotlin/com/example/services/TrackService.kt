package com.example.services

import com.example.models.Track
import com.example.repository.Tracks
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class TrackService(private val s3Service: S3Service) {

    // CREAR CANCIÓN
    suspend fun create(name: String, duration: Int, albumId: UUID?, artistId: UUID, audioBytes: ByteArray?): Track {
        var audioKey: String? = null

        // Si nos envían el archivo, lo subimos como AUDIO
        if (audioBytes != null) {
            // Nota el "audio/mpeg" aquí abajo
            audioKey = s3Service.uploadFile("track-$name.mp3", audioBytes, "audio/mpeg")
        }

        val newId = dbQuery {
            val id = UUID.randomUUID()
            Tracks.insert {
                it[Tracks.id] = id
                it[Tracks.name] = name
                it[Tracks.duration] = duration
                it[Tracks.previewUrl] = audioKey
                it[Tracks.albumId] = albumId
                it[Tracks.artistId] = artistId
            }
            id
        }

        // Generamos la URL firmada si hay audio
        val signedUrl = audioKey?.let { s3Service.getPresignedUrl(it) }

        return Track(newId, name, duration, signedUrl, albumId, artistId)
    }

    // LISTAR CANCIONES DE UN ÁLBUM
    suspend fun getByAlbum(albumId: UUID): List<Track> {
        return dbQuery {
            Tracks.selectAll().where { Tracks.albumId eq albumId }.map {
                val url = it[Tracks.previewUrl]?.let { key -> s3Service.getPresignedUrl(key) }
                Track(
                    it[Tracks.id],
                    it[Tracks.name],
                    it[Tracks.duration],
                    url,
                    it[Tracks.albumId],
                    it[Tracks.artistId]
                )
            }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
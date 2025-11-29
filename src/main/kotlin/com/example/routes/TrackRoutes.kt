

package com.example.routes

import com.example.services.TrackService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.trackRoutes(trackService: TrackService) {
    route("/tracks") {

        // GET: Ver canciones de un álbum
        get("/album/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                try {
                    call.respond(trackService.getByAlbum(UUID.fromString(id)))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        // POST: Subir canción (Requiere Token)
        authenticate("auth-jwt") {
            post {
                val multipart = call.receiveMultipart()
                var name = ""
                var duration = 0
                var albumId: UUID? = null
                var artistId: UUID? = null
                var audioBytes: ByteArray? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "name" -> name = part.value
                                "duration" -> duration = part.value.toIntOrNull() ?: 0
                                "albumId" -> albumId = try { UUID.fromString(part.value) } catch (e: Exception) { null }
                                "artistId" -> artistId = try { UUID.fromString(part.value) } catch (e: Exception) { null }
                            }
                        }
                        is PartData.FileItem -> {
                            if (part.name == "audio") { // En Postman el campo será 'audio'
                                audioBytes = part.streamProvider().readBytes()
                            }
                        }
                        else -> part.dispose()
                    }
                    part.dispose()
                }

                if (name.isNotEmpty() && artistId != null) {
                    val track = trackService.create(name, duration, albumId, artistId!!, audioBytes)
                    call.respond(HttpStatusCode.Created, track)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Faltan datos (name, artistId, audio)")
                }
            }
        }
    }
}
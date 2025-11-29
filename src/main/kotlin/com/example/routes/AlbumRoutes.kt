

package com.example.routes

import com.example.services.AlbumService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.albumRoutes(albumService: AlbumService) {
    route("/albums") {

        // GET: Ver todos
        get { call.respond(albumService.getAll()) }

        // GET: Ver álbumes de un artista específico (/albums/artist/123-abc...)
        get("/artist/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                try {
                    val artistId = UUID.fromString(id)
                    call.respond(albumService.getByArtist(artistId))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        // POST: Crear nuevo álbum (Requiere Token)
        authenticate("auth-jwt") {
            post {
                val multipart = call.receiveMultipart()
                var name = ""
                var year = 0
                var artistId: UUID? = null
                var imageBytes: ByteArray? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "name" -> name = part.value
                                "year" -> year = part.value.toIntOrNull() ?: 0
                                "artistId" -> artistId = try { UUID.fromString(part.value) } catch (e: Exception) { null }
                            }
                        }
                        is PartData.FileItem -> {
                            if (part.name == "image") {
                                imageBytes = part.streamProvider().readBytes()
                            }
                        }
                        else -> part.dispose()
                    }
                    part.dispose()
                }

                if (name.isNotEmpty() && year > 0 && artistId != null && imageBytes != null) {
                    val album = albumService.create(name, year, artistId!!, imageBytes!!)
                    call.respond(HttpStatusCode.Created, album)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Faltan datos (name, year, artistId, image)")
                }
            }
        }
    }
}
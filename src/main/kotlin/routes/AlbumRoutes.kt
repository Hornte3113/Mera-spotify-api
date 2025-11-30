package com.example.routes

import com.example.models.AlbumInput
import com.example.services.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.albumRoutes() {
    val albumService = AlbumService()

    route("/albumes") {
        // Crear álbum - POST /api/albumes
        post {
            try {
                val albumInput = call.receive<AlbumInput>()
                val album = albumService.create(albumInput)
                if (album != null) {
                    call.respond(HttpStatusCode.Created, album)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al crear el álbum"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Error desconocido")))
            }
        }

        // Obtener todos los álbumes - GET /api/albumes
        get {
            try {
                val albumes = albumService.readAll()
                call.respond(HttpStatusCode.OK, albumes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al obtener álbumes")))
            }
        }

        // Obtener álbum por ID - GET /api/albumes/{id}
        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val album = albumService.read(id)
                if (album != null) {
                    call.respond(HttpStatusCode.OK, album)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Álbum no encontrado"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al obtener álbum")))
            }
        }

        // Actualizar álbum - PUT /api/albumes/{id}
        put("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val albumInput = call.receive<AlbumInput>()
                val updated = albumService.update(id, albumInput)
                if (updated) {
                    val album = albumService.read(id)
                    call.respond(HttpStatusCode.OK, album!!)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Álbum no encontrado"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "ID o datos inválidos")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Error al actualizar álbum")))
            }
        }

        // Eliminar álbum - DELETE /api/albumes/{id}
        delete("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val deleted = albumService.delete(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Álbum eliminado correctamente"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Álbum no encontrado"))
                }
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al eliminar álbum")))
            }
        }
    }
}

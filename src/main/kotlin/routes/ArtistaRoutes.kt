package com.example.routes

import com.example.models.ArtistaInput
import com.example.services.ArtistaService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.artistaRoutes() {
    val artistaService = ArtistaService()

    route("/artistas") {
        // Crear artista - POST /api/artistas
        post {
            try {
                val artistaInput = call.receive<ArtistaInput>()
                val artista = artistaService.create(artistaInput)
                if (artista != null) {
                    call.respond(HttpStatusCode.Created, artista)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al crear el artista"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Error desconocido")))
            }
        }

        // Obtener todos los artistas - GET /api/artistas
        get {
            try {
                val artistas = artistaService.readAll()
                call.respond(HttpStatusCode.OK, artistas)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al obtener artistas")))
            }
        }

        // Obtener artista por ID - GET /api/artistas/{id}
        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val artista = artistaService.read(id)
                if (artista != null) {
                    call.respond(HttpStatusCode.OK, artista)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Artista no encontrado"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al obtener artista")))
            }
        }

        // Actualizar artista - PUT /api/artistas/{id}
        put("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val artistaInput = call.receive<ArtistaInput>()
                val updated = artistaService.update(id, artistaInput)
                if (updated) {
                    val artista = artistaService.read(id)
                    call.respond(HttpStatusCode.OK, artista!!)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Artista no encontrado"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Error al actualizar artista")))
            }
        }

        // Eliminar artista - DELETE /api/artistas/{id}
        delete("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val deleted = artistaService.delete(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Artista eliminado correctamente"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Artista no encontrado"))
                }
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al eliminar artista")))
            }
        }
    }
}

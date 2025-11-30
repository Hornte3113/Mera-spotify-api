package com.example.routes

import com.example.models.TrackInput
import com.example.services.TrackService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.trackRoutes() {
    val trackService = TrackService()

    route("/tracks") {
        // Crear track - POST /api/tracks
        post {
            try {
                val trackInput = call.receive<TrackInput>()
                val track = trackService.create(trackInput)
                if (track != null) {
                    call.respond(HttpStatusCode.Created, track)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al crear el track"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Error desconocido")))
            }
        }

        // Obtener todos los tracks - GET /api/tracks
        get {
            try {
                val tracks = trackService.readAll()
                call.respond(HttpStatusCode.OK, tracks)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al obtener tracks")))
            }
        }

        // Obtener track por ID - GET /api/tracks/{id}
        get("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val track = trackService.read(id)
                if (track != null) {
                    call.respond(HttpStatusCode.OK, track)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Track no encontrado"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al obtener track")))
            }
        }

        // Actualizar track - PUT /api/tracks/{id}
        put("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val trackInput = call.receive<TrackInput>()
                val updated = trackService.update(id, trackInput)
                if (updated) {
                    val track = trackService.read(id)
                    call.respond(HttpStatusCode.OK, track!!)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Track no encontrado"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "ID o datos inválidos")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Error al actualizar track")))
            }
        }

        // Eliminar track - DELETE /api/tracks/{id}
        delete("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"])
                val deleted = trackService.delete(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Track eliminado correctamente"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Track no encontrado"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al eliminar track")))
            }
        }
    }
}

package com.example.com.example.routes




import com.example.com.example.services.ArtistService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.artistRoutes(artistService: ArtistService) {
    route("/artists") {
        get { call.respond(artistService.getAll()) }

        authenticate("auth-jwt") {
            post {
                val multipart = call.receiveMultipart()
                var name = ""
                var genre = ""
                var imageBytes: ByteArray? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "name") name = part.value
                            if (part.name == "genre") genre = part.value
                        }
                        is PartData.FileItem -> {
                            if (part.name == "image") imageBytes = part.streamProvider().readBytes()
                        }
                        else -> part.dispose()
                    }
                    part.dispose()
                }

                if (name.isNotEmpty() && imageBytes != null) {
                    val artist = artistService.create(name, genre, imageBytes!!)
                    call.respond(HttpStatusCode.Created, artist)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Faltan datos")
                }
            }
        }
    }
}
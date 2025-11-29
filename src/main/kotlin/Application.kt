package com.example

import io.ktor.server.application.*
import io.ktor.server.netty.*
 // (Si tus plugins están aquí, déjalo así)
// IMPORTA LOS SERVICIOS CORREGIDOS
import com.example.com.example.services.AuthService
import com.example.services.S3Service
import com.example.services.ArtistService
import com.example.com.example.routes.authRoutes
import com.example.routes.artistRoutes // <--- Ahora sí lo encontrará
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import com.example.services.AlbumService // Nuevo Import
import com.example.routes.albumRoutes   // Nuevo Import

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureSecurity()

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.domain").getString()
    val audience = environment.config.property("jwt.audience").getString()


    val authService = AuthService(secret, issuer, audience)

    // Inicialización limpia
    val s3Service = S3Service(environment.config)
    val artistService = ArtistService(s3Service)
    val albumService = AlbumService(s3Service)

    routing {
        authRoutes(authService)
        artistRoutes(artistService) // <--- Ya no debería marcar rojo

        albumRoutes(albumService)

        get("/") {
            call.respondText("Spotify API is Running! Gracias a Dios")
        }
    }
}
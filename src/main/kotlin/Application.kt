package com.example

import io.ktor.server.application.*
import io.ktor.server.netty.*
import com.example.com.api.plugins.* // (Si tus plugins están aquí, déjalo así)
// IMPORTA LOS SERVICIOS CORREGIDOS
import com.example.com.example.services.AuthService
import com.example.services.S3Service
import com.example.services.ArtistService
import com.example.com.example.routes.authRoutes
import com.example.routes.artistRoutes // <--- Ahora sí lo encontrará
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

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

    routing {
        authRoutes(authService)
        artistRoutes(artistService) // <--- Ya no debería marcar rojo

        get("/") {
            call.respondText("Spotify API is Running!")
        }
    }
}
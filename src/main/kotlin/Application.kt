package com.example

import io.ktor.server.application.*
import io.ktor.server.netty.*
// Asegúrate de que este import apunte a donde tienes tus plugins (Security, Serialization, etc.)
import com.example.plugins.* // Imports de Servicios
import com.example.com.example.services.AuthService
import com.example.services.S3Service
import com.example.services.ArtistService
import com.example.services.AlbumService
import com.example.services.TrackService // <--- Nuevo

// Imports de Rutas
import com.example.com.example.routes.authRoutes
import com.example.routes.artistRoutes
import com.example.routes.albumRoutes
import com.example.routes.trackRoutes   // <--- Nuevo

import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    // 1. Cargar Plugins
    configureSerialization()
    configureDatabases()
    configureSecurity()

    // 2. Configuración JWT (Auth)
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.domain").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val authService = AuthService(secret, issuer, audience)

    // 3. Inicializar Servicios
    val s3Service = S3Service(environment.config)

    val artistService = ArtistService(s3Service)
    val albumService = AlbumService(s3Service)
    val trackService = TrackService(s3Service) // <--- Inicializamos el servicio de canciones

    // 4. Registrar Rutas
    routing {
        authRoutes(authService)
        artistRoutes(artistService)
        albumRoutes(albumService)
        trackRoutes(trackService) // <--- Habilitamos la URL /tracks

        get("/") {
            call.respondText("Spotify API is Running w/ Tracks!")
        }
    }
}
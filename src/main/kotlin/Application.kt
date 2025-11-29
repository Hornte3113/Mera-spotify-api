package com.example

import io.ktor.server.application.*
import io.ktor.server.netty.*
import com.example.com.api.plugins.*
import com.example.com.example.services.AuthService
import com.example.com.example.routes.authRoutes
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    // 1. Plugins Base
    configureSerialization()
    configureDatabases()

    // 2. Seguridad
    configureSecurity()

    // 3. Inicializar Servicios
    // Leemos datos para AuthService
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.domain").getString()
    val audience = environment.config.property("jwt.audience").getString()

    val authService = AuthService(secret, issuer, audience)

    // 4. Rutas
    routing {
        authRoutes(authService)

        // Aquí puedes poner una ruta de prueba
        get("/") {
            call.respondText("Spotify API is Running!")
        }
    }
}
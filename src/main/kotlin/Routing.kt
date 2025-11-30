package com.example

import com.example.routes.albumRoutes
import com.example.routes.artistaRoutes
import com.example.routes.trackRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            artistaRoutes()
            albumRoutes()
            trackRoutes()
        }
        
        get("/") {
            call.respondText("API de Catálogo Musical - Ktor Backend")
        }
    }
}

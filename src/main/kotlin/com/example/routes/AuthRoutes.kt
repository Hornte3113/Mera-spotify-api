package com.example.com.example.routes


import com.example.com.example.models.UserAuthParams
import com.example.com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {

        // POST /auth/login
        post("/login") {
            try {
                val creds = call.receive<UserAuthParams>()
                val token = authService.login(creds)
                if (token != null) {
                    call.respond(mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos inválidos")
            }
        }

        // POST /auth/register
        post("/register") {
            try {
                val creds = call.receive<UserAuthParams>()
                val token = authService.register(creds)
                if (token != null) {
                    call.respond(HttpStatusCode.Created, mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Conflict, "El usuario ya existe")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos inválidos")
            }
        }
    }
}
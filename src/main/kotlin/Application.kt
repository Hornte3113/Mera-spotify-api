package com.example


import io.ktor.server.application.*
import io.ktor.server.netty.*
import com.example.com.api.plugins.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureDatabases()
    // configureSecurity() // Lo haremos luego
    // configureRouting() // Lo haremos luego
}
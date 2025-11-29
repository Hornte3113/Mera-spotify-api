package com.example.com.example.models



import java.util.UUID

data class User(
    val id: UUID,
    val username: String,
    val role: String
)

// Clase para recibir los datos del Login/Registro desde el JSON
data class UserAuthParams(
    val username: String,
    val password: String
)
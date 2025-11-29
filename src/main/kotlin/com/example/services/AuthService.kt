package com.example.com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.Users
import com.example.com.example.models.UserAuthParams
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class AuthService(
    private val secret: String,
    private val issuer: String,
    private val audience: String
) {
    // Función para crear el token (se llama cuando el login es exitoso)
    private fun generateToken(username: String, role: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // Expira en 24 horas
            .sign(Algorithm.HMAC256(secret))
    }

    // LOGIN
    suspend fun login(creds: UserAuthParams): String? = dbQuery {
        // Buscamos el usuario en la BD
        val userRow = Users.selectAll()
            .where { Users.username eq creds.username }
            .singleOrNull() ?: return@dbQuery null

        // Comparamos contraseñas (Para producción deberías usar BCrypt aquí)
        if (userRow[Users.password] == creds.password) {
            generateToken(userRow[Users.username], userRow[Users.role])
        } else {
            null
        }
    }

    // REGISTRO (Crear nuevo usuario)
    suspend fun register(creds: UserAuthParams): String? = dbQuery {
        // Verificar si ya existe
        val existing = Users.selectAll().where { Users.username eq creds.username }.singleOrNull()
        if (existing != null) return@dbQuery null

        Users.insert {
            it[id] = UUID.randomUUID()
            it[username] = creds.username
            it[password] = creds.password // Aquí deberías hashear la contraseña
            it[role] = "USER" // Por defecto todos son usuarios normales
        }

        // Generamos el token directamente para que entre logueado
        generateToken(creds.username, "USER")
    }

    // Helper para consultas a base de datos
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
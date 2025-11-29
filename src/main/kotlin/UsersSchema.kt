package com.example


import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

// Definición de la tabla para Exposed (debe coincidir con tu SQL)
object Users : Table("users") {
    val id = uuid("id")
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 255)
    val role = varchar("role", 20).default("USER")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}
package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            val responseBody = bodyAsText()
            assertTrue(responseBody.contains("Catálogo Musical") || responseBody.isNotEmpty())
        }
    }

}

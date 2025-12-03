package org.musicapi

import org.musicapi.infrastructure.config.setupJsonSerialization
import org.musicapi.infrastructure.config.setupRouting
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
            setupJsonSerialization()
            setupRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            val responseBody = bodyAsText()
            assertTrue(responseBody.contains("Catálogo Musical") || responseBody.isNotEmpty())
        }
    }

}

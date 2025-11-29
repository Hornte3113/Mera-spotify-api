package com.example.com.example.services


import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.collections.Attributes
import aws.smithy.kotlin.runtime.content.ByteStream
import io.ktor.server.config.*
import java.util.UUID
import kotlin.time.Duration.Companion.hours

class S3Service(config: ApplicationConfig) {
    // Leemos las credenciales del archivo application.yaml
    private val bucketName = config.property("aws.bucketName").getString()
    private val region = config.property("aws.region").getString()
    private val accessKey = config.property("aws.accessKey").getString()
    private val secretKey = config.property("aws.secretKey").getString()

    // Configuración de autenticación para AWS
    private fun getCredentialsProvider(): CredentialsProvider {
        return object : CredentialsProvider {
            override suspend fun resolve(attributes: Attributes): Credentials {
                return Credentials(accessKey, secretKey)
            }
        }
    }

    // 1. SUBIR ARCHIVO (Imagen o Audio)
    suspend fun uploadFile(fileName: String, fileBytes: ByteArray, contentType: String): String {
        // Generamos un nombre único para no sobrescribir archivos (ej: "uuid-foto.jpg")
        val uniqueName = "${UUID.randomUUID()}-$fileName"

        S3Client.fromEnvironment {
            this.region = this@S3Service.region
            this.credentialsProvider = getCredentialsProvider()
        }.use { s3 ->
            val request = PutObjectRequest {
                bucket = bucketName
                key = uniqueName
                body = ByteStream.fromBytes(fileBytes)
                this.contentType = contentType
            }
            s3.putObject(request)
        }

        // Devolvemos el nombre único (Key) para guardarlo en la base de datos
        return uniqueName
    }

    // 2. GENERAR URL TEMPORAL (Para ver imágenes privadas)
    suspend fun getPresignedUrl(objectKey: String): String {
        S3Client.fromEnvironment {
            this.region = this@S3Service.region
            this.credentialsProvider = getCredentialsProvider()
        }.use { s3 ->
            val request = GetObjectRequest {
                bucket = bucketName
                key = objectKey
            }

            // La URL será válida por 12 horas
            val presignedRequest = s3.presignGetObject(request, duration = 12.hours)
            return presignedRequest.url.toString()
        }
    }
}
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.postgresql)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)


        // Ktor Server (Core, Netty, Auth, JSON) - Ya deberían estar si usaste el generador
        implementation("io.ktor:ktor-server-core-jvm")
        implementation("io.ktor:ktor-server-netty-jvm")
        implementation("io.ktor:ktor-server-auth-jvm")
        implementation("io.ktor:ktor-server-auth-jwt-jvm")
        implementation("io.ktor:ktor-server-content-negotiation-jvm")
        implementation("io.ktor:ktor-serialization-jackson-jvm")

        // Base de Datos
        implementation("org.jetbrains.exposed:exposed-core:0.50.1")
        implementation("org.jetbrains.exposed:exposed-dao:0.50.1")
        implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
        implementation("org.jetbrains.exposed:exposed-java-time:0.50.1") // Para fechas
        implementation("org.postgresql:postgresql:42.7.2")
        implementation("com.zaxxer:HikariCP:5.1.0") // <-- IMPORTANTE: Pool de conexiones

        // AWS S3 (Para subir imágenes)
        implementation("aws.sdk.kotlin:s3:1.0.0")

        // Logging
        implementation("ch.qos.logback:logback-classic:1.4.14")
    dependencies {
        // ... tus otras dependencias ...

        // AÑADE ESTA LÍNEA:
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    }

}

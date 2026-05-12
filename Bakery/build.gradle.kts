plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.7"
}

group = "org.bakery_tm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-server-auth:2.3.7")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.7")
    implementation("io.ktor:ktor-server-call-logging:2.3.7")

    implementation("io.ktor:ktor-auth:2.3.7")
    implementation("io.ktor:ktor-auth-jwt:2.3.7")
    implementation("com.auth0:java-jwt:4.4.0") // Для работы с JWT токенами

    // Swagger
    implementation("io.github.smiley4:ktor-swagger-ui:2.7.0")

    // Koin
    implementation("io.insert-koin:koin-ktor:3.5.3")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.3")

    // База данных
    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.0.1")  // Пул соединений

    // Логирование
    implementation("ch.qos.logback:logback-classic:1.4.11")

    implementation("at.favre.lib:bcrypt:0.9.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
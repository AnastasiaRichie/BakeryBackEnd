package org.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.example.db.DatabaseFactory
import org.example.di.appModule
import org.example.models.JwtConfig
import org.example.routes.authRoutes
import org.example.routes.productRoutes
import org.example.routes.orderRoutes
import org.example.routes.orderWebSockets
import org.example.service.ProductService
import org.example.service.OrderService
import org.example.service.UserService
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.time.Duration

fun Application.module() {
    val config = environment.config
    val url = config.property("db.url").getString()
    val driver = config.property("db.driver").getString()
    val username = config.property("db.username").getString()
    val password = config.property("db.password").getString()

    val jwtSection = environment.config.config("jwt")

    val jwtConfig = JwtConfig(
        secret = jwtSection.property("secret").getString(),
        issuer = jwtSection.property("issuer").getString(),
        audience = jwtSection.property("audience").getString(),
        realm = jwtSection.property("realm").getString(),
        expiresIn = jwtSection.property("expiresIn").getString().toLong() // 30L * 24 * 60 * 60 * 1000
    )

    runBlocking {
        DatabaseFactory.init(url, driver, username, password)
    }
    configureDi(jwtConfig)
    configureSerialization()
    configureSecurity(jwtConfig)
    configureRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
}

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(JWT.require(Algorithm.HMAC256(jwtConfig.secret)).withAudience(jwtConfig.audience).withIssuer(jwtConfig.issuer).build())
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) JWTPrincipal(credential.payload)
                else null
            }
        }
    }
}

fun Application.configureRouting() {
    val userService by inject<UserService>()
    val productService by inject<ProductService>()
    val orderService by inject<OrderService>()

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(30)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        route("/api") {
            authRoutes(userService)
            orderRoutes(orderService)
            productRoutes(productService)
            orderWebSockets()
        }
    }
}

fun Application.configureDi(jwtConfig: JwtConfig) {
    install(Koin) {
        slf4jLogger()
        modules(appModule(jwtConfig))
    }
}
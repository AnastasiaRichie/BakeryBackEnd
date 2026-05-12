package org.bakery_tm.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

fun Application.configureAuth() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "your_realm"
            verifier(
                JWT.require(Algorithm.HMAC256("your_secret_key"))
                    .withAudience("your_audience")
                    .withIssuer("your_issuer")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("id").asLong() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

fun generateJwtToken(userId: Long): String {
    return JWT.create()
        .withAudience("your_audience")
        .withIssuer("your_issuer")
        .withClaim("id", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
        .sign(Algorithm.HMAC256("your_secret_key"))
}
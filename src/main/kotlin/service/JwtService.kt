package org.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.example.models.JwtConfig
import java.util.Date

class JwtService(private val config: JwtConfig) {

    fun generateToken(userId: Int): String {
        val now = System.currentTimeMillis()
        val expiresAt = Date(now + config.expiresIn)

        return JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withClaim("userId", userId)
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC256(config.secret))
    }
}
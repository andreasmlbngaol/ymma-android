package com.andreasmlbngaol.ymma.controllers.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.config.ApplicationConfig
import java.util.Date

object JwtConfig {
    private lateinit var secretKey: String
    lateinit var algorithm: Algorithm
        private set

    const val ACCESS_TOKEN_EXPIRATION_DURATION_IN_SECOND = 60 * 60       // 1 jam
    const val REFRESH_TOKEN_EXPIRATION_DURATION_IN_SECOND = 60 * 60 * 24 * 30 // 30 hari

    fun init(config: ApplicationConfig) {
        secretKey = config.config("ktor.jwt").property("secretKey").getString()
        algorithm = Algorithm.HMAC256(secretKey)
    }

    fun generateAccessToken(
        userId: Long,
        name: String,
        email: String,
        username: String?
    ): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withClaim("sub", userId)
            .withClaim("name", name)
            .withClaim("email", email)
            .withClaim("username", username)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + ACCESS_TOKEN_EXPIRATION_DURATION_IN_SECOND * 1000L))
            .sign(algorithm)
    }

    fun generateRefreshToken(userId: Long): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withClaim("sub", userId)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + REFRESH_TOKEN_EXPIRATION_DURATION_IN_SECOND * 1000L))
            .sign(algorithm)
    }

    fun getUserIdFromToken(token: String): Long? {
        return try {
            val decodedJWT = JWT.require(algorithm).build().verify(token)
            decodedJWT.getClaim("sub").asLong()
        } catch (_: Exception) {
            null
        }
    }
}

fun JWTPrincipal.userId(): Long =
    this.payload.getClaim("sub").asLong()

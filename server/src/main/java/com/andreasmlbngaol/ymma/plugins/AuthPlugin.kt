package com.andreasmlbngaol.ymma.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.oauth

fun Application.authenticationPlugin(
    httpClient: HttpClient
) {
    val secretKey = environment.config.config("ktor.jwt").property("secretKey").getString()
    install(Authentication) {
        jwt(AuthNames.JWT_AUTH) {
            realm = "Access to the application"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secretKey))
                    .build()
            )
            validate { credential ->
                val payload = credential.payload
                val id = payload.getClaim("sub").asLong()

                if (id != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
        oauth(AuthNames.GOOGLE_O_AUTH) {
            urlProvider = { "http://localhost:8080/api/auth/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf(
                        "https://www.googleapis.com/auth/userinfo.profile",
                        "https://www.googleapis.com/auth/userinfo.email"
                    ),
                    extraAuthParameters = listOf("access_type" to "offline")
                )
            }

            client = httpClient
        }
    }
}

object AuthNames {
    const val JWT_AUTH = "basic-auth"
    const val GOOGLE_O_AUTH = "google-o-auth"
}
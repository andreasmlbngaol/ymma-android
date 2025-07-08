package com.andreasmlbngaol.ymma.plugins

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

suspend fun fetchGoogleUserInfo(
    httpClient: HttpClient,
    accessToken: String
): UserInfo? {
    val response = httpClient.get("https://www.googleapis.com/oauth2/v3/userinfo") {
        bearerAuth(accessToken)
    }

    if (response.status == HttpStatusCode.OK) {
        val googleUser = response.body<GoogleUserResponse>()
        return UserInfo(
            userId = googleUser.sub.trim(),
            name = googleUser.name.trim(),
            email = googleUser.email.trim(),
            imageUrl = googleUser.picture.trim()
        )
    }
    return null
}
@Serializable
data class UserInfo(
    val userId: String,
    val name: String,
    val email: String,
    val imageUrl: String? = null
)

@Serializable
data class GoogleUserResponse(
    val sub: String,
    val name: String,
    val givenName: String,
    val familyName: String,
    val picture: String,
    val email: String,
    val emailVerified: Boolean
)


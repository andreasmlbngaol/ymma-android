package com.andreasmlbngaol.ymma.controllers.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.andreasmlbngaol.ymma.database.dao.UsersDao
import com.andreasmlbngaol.ymma.domains.auth.*
import com.andreasmlbngaol.ymma.email.EmailService.sendEmail
import com.andreasmlbngaol.ymma.plugins.AuthNames
import com.andreasmlbngaol.ymma.plugins.AuthNames.JWT_AUTH
import com.andreasmlbngaol.ymma.plugins.fetchGoogleUserInfo
import com.andreasmlbngaol.ymma.type.Provider
import com.andreasmlbngaol.ymma.utils.respondJson
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.coroutineScope

fun Route.authRoute(httpClient: HttpClient) {
    route("/auth") {
        // Android oauth
        post("/google-oauth") {
            val request = call.receive<GoogleLoginRequest>()
            val idTokenString = request.idToken

            val webClientId = System.getProperty("GOOGLE_WEB_CLIENT_ID")
                ?: return@post call.respondJson(HttpStatusCode.InternalServerError, "Google Web Client ID not found")

            val verifier = GoogleIdTokenVerifier.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance()
            )
                .setAudience(listOf(webClientId))
                .build()

            val idToken = verifier.verify(idTokenString)

            if (idToken != null) {
                val payload = idToken.payload
                val userId = payload.subject
                val email = payload.email
                val name = payload["name"] as? String ?: email.substringBefore("@")
                val picture = payload["picture"] as? String
                val emailVerified = payload.emailVerified

                var user = UsersDao.findByProviderAndSub(provider = Provider.Google, sub = userId)

                if (user == null) {
                    user = UsersDao.findByEmail(email)
                    if (user != null) {
                        UsersDao.updateOAuth(
                            id = user.id,
                            oAuthProvider = Provider.Google,
                            oAuthSub = userId
                        )
                    } else {
                        val id = UsersDao.create(
                            name = name,
                            email = email,
                            imageUrl = picture,
                            isVerified = emailVerified,
                            oAuthProvider = Provider.Google,
                            oAuthSub = userId
                        )

                        user = User(
                            id = id.value,
                            name = name,
                            email = email,
                            imageUrl = picture,
                            isVerified = emailVerified
                        )
                    }
                }

                setCookieAndRespondToken(user) // atau respond JSON token
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid ID token")
            }
        }

        authenticate(AuthNames.GOOGLE_O_AUTH) {
            get("/login") {}

            get("/callback") {
                val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                    ?: return@get call.respondJson(HttpStatusCode.Unauthorized, "OAuth Login Failed")

                val accessToken = principal.accessToken

                val googleUserInfo = fetchGoogleUserInfo(
                    httpClient = httpClient,
                    accessToken = accessToken
                ) ?: return@get call.respondJson(HttpStatusCode.Unauthorized, "OAuth Login Failed")

                var user = UsersDao
                    .findByProviderAndSub(sub = googleUserInfo.userId)

                if(user == null) {
                    user = UsersDao.findByEmail(googleUserInfo.email)

                    if(user != null) {
                        UsersDao.updateOAuth(
                            id = user.id,
                            oAuthProvider = Provider.Google,
                            oAuthSub = googleUserInfo.userId
                        )
                    } else {

                        val id = UsersDao.create(
                            name = googleUserInfo.name,
                            email = googleUserInfo.email,
                            imageUrl = googleUserInfo.imageUrl,
                            isVerified = true,
                            oAuthProvider = Provider.Google,
                            oAuthSub = googleUserInfo.userId
                        )

                        user = User(
                            id = id.value,
                            name = googleUserInfo.name,
                            email = googleUserInfo.email,
                            isVerified = true,
                            imageUrl = googleUserInfo.imageUrl
                        )
                    }
                }

                setCookieAndRespondToken(user)
            }
        }

        post("/login") {
            val payload = call.receive<LoginRequest>()
            val identifier = payload.identifier.trim()

            val user = UsersDao
                .findByUsername(identifier)
                ?: UsersDao.findByEmail(identifier)
                ?: return@post call.respondJson(HttpStatusCode.Unauthorized, "Invalid credentials")

            val isPasswordMatch = BCrypt.verifyer().verify(
                payload.password.toByteArray(),
                user.passwordHashed?.toByteArray()
            ).verified

            if(!isPasswordMatch) return@post call.respondJson(HttpStatusCode.Unauthorized, "Invalid credentials")

            setCookieAndRespondToken(user)
        }

        post("/register") {
            val payload = call.receive<RegisterRequest>()

            val email = payload.email.trim()
            val password = payload.password.trim()
            val name = payload.name.trim()

            if(!validateEmail(email)) return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid email format")
            if(!validatePassword(password)) return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid password format. Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number.")

            if(UsersDao.existByEmail(email)) return@post call.respondJson(HttpStatusCode.Conflict, "Email already exists")

            val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())

            val id = UsersDao.create(
                name = name,
                email = email,
                passwordHashed = hashedPassword
            )

            val confirmationUrl = "https://your-app.com/verify?token=test-ajakok"

            val content = """
                <h3>Konfirmasi Email Anda</h3>
                <p>Silakan klik link di bawah ini untuk mengonfirmasi email Anda:</p>
                <a href="$confirmationUrl">Verifikasi Email</a>
            """.trimIndent()

            coroutineScope {
                sendEmail(
                    to = email,
                    subject = "Konfirmasi Email",
                    content = content
                )
            }

            call.respond(
                HttpStatusCode.Created,
                LoginResponse(
                    id = id.value,
                    name = name,
                    email = email,
                    isVerified = false,
                )
            )
        }

        post("/refresh") {

            val refreshToken = call.request.cookies["refresh_token"]
                ?: return@post call.respondJson(HttpStatusCode.Unauthorized, "Invalid refresh token")

            val userId = JwtConfig.getUserIdFromToken(refreshToken)
                ?: return@post call.respondJson(HttpStatusCode.Unauthorized, "Invalid refresh token")

            val user = UsersDao.findById(userId)
                ?: return@post call.respondJson(HttpStatusCode.Unauthorized, "User not found")

            setCookieAndRespondToken(user)
        }

        authenticate(JWT_AUTH) {
            post("/logout") {
                call.response.cookies.append(
                    Cookie(
                        name = "refresh_token",
                        value = "",
                        path = "/",
                        httpOnly = true,
                        maxAge = 0
                    )
                )

                call.response.cookies.append(
                    Cookie(
                        name = "access_token",
                        value = "",
                        path = "/",
                        httpOnly = true,
                        maxAge = 0
                    )
                )

                call.respondJson(
                    HttpStatusCode.OK,
                    "Logout successful"
                )
            }

            route("/settings") {
                post("/password") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val userId = principal.payload.getClaim("sub").asLong()

                    val payload = call.receive<ChangePasswordRequest>()

                    val oldPassword = payload.oldPassword.trim()
                    val newPassword = payload.newPassword.trim()

                    val isPasswordMatch = BCrypt.verifyer().verify(
                        oldPassword.toByteArray(),
                        UsersDao.findById(userId)?.passwordHashed?.toByteArray()
                    ).verified

                    if(!isPasswordMatch) return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid old password")
                    if(!validatePassword(newPassword)) return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid new password format. Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number.")

                    val hashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
                    val updatedRows = UsersDao.changePassword(userId, hashedPassword)
                    if(updatedRows == 0) return@post call.respondJson(HttpStatusCode.InternalServerError, "Error while updating password")

                    call.respondJson(
                        HttpStatusCode.OK,
                        "Password updated successfully"
                    )
                }

                post("/profile-picture") {

                }
            }
        }
    }
}

suspend fun RoutingContext.setCookieAndRespondToken(user: User) {
    val accessToken = JwtConfig.generateAccessToken(user.id, user.name, user.email, user.username)
    val refreshToken = JwtConfig.generateRefreshToken(user.id)

    call.response.cookies.append(
        Cookie(
            name = "refresh_token",
            value = refreshToken,
            path = "/",
            httpOnly = true,
            maxAge = JwtConfig.REFRESH_TOKEN_EXPIRATION_DURATION_IN_SECOND
        )
    )

    call.response.cookies.append(
        Cookie(
            name = "access_token",
            value = accessToken,
            path = "/",
            httpOnly = true,
            maxAge = JwtConfig.ACCESS_TOKEN_EXPIRATION_DURATION_IN_SECOND
        )
    )

    call.respond(
        LoginResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            username = user.username,
            isVerified = user.isVerified,
            imageUrl = user.imageUrl,
            accessToken = accessToken
        )
    )
}

private fun validateEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    return email.isNotBlank() && email.matches(emailRegex)
}

private fun validatePassword(password: String): Boolean {
    val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$".toRegex()
    return password.matches(passwordRegex)
}

@Suppress("unused")
private fun validateUsername(username: String): Boolean {
    val usernameRegex = "^[a-z0-9._]{4,}$".toRegex()
    return username.matches(usernameRegex)
}

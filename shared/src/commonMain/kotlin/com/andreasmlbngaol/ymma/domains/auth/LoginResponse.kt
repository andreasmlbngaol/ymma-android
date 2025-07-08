package com.andreasmlbngaol.ymma.domains.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val id: Long,
    val name: String,
    val email: String,
    val username: String? = null,
    val isVerified: Boolean = false,
    val imageUrl: String? = null,
    val accessToken: String? = null
)

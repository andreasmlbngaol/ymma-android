package com.andreasmlbngaol.ymma.domains.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

package com.andreasmlbngaol.ymma.domains.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

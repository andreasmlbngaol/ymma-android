package com.andreasmlbngaol.ymma.domains.auth

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

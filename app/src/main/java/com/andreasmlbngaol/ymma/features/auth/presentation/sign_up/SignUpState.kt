package com.andreasmlbngaol.ymma.features.auth.presentation.sign_up

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisibility: Boolean = false,
    val confirmPassword: String = ""
)
package com.andreasmlbngaol.ymma.features.auth.presentation.sign_in

data class SignInState(
    val identifier: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val signInError: String? = null,
    val isLoading: Boolean = false
)

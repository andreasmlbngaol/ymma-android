package com.andreasmlbngaol.ymma.features.auth.presentation.sign_up

sealed interface SignUpAction {
    data class OnNameChange(val name: String) : SignUpAction
    data class OnEmailChange(val email: String) : SignUpAction
    data class OnPasswordChange(val password: String) : SignUpAction
    data object OnPasswordVisibilityToggle : SignUpAction
    data class OnConfirmPasswordChange(val confirmPassword: String) : SignUpAction
    data object OnSignUp : SignUpAction
    data class OnSignUpWithGoogle(val token: String) : SignUpAction
    data object OnNavigateToSignIn : SignUpAction
    data object OnNavigateBack : SignUpAction
}
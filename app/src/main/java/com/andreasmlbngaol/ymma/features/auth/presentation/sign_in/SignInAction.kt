package com.andreasmlbngaol.ymma.features.auth.presentation.sign_in

sealed interface SignInAction {
    data class OnIdentifierChange(val identifier: String) : SignInAction
    data class OnPasswordChange(val password: String) : SignInAction
    data object OnPasswordVisibilityToggle : SignInAction
    data object OnSignInWithIdentifierAndPassword : SignInAction
    data class OnSignInWithGoogle(val token: String) : SignInAction
    data object OnNavigateBack : SignInAction
    data object OnNavigateToSignUp : SignInAction
    data object OnNavigateToResetPassword : SignInAction
}
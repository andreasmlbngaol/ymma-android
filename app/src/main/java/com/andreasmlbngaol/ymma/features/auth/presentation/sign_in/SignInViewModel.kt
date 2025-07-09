package com.andreasmlbngaol.ymma.features.auth.presentation.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreasmlbngaol.ymma.core.data.ApiRoutes
import com.andreasmlbngaol.ymma.domains.auth.GoogleLoginRequest
import com.andreasmlbngaol.ymma.domains.auth.LoginRequest
import com.andreasmlbngaol.ymma.domains.auth.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    val httpClient: HttpClient
): ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onAction(action: SignInAction) {
        when(action) {
            is SignInAction.OnIdentifierChange -> {
                _state.update {
                    it.copy(identifier = action.identifier.lowercase().trim())
                }
            }
            is SignInAction.OnPasswordChange -> {
                _state.update {
                    it.copy(password = action.password.trim())
                }
            }
            is SignInAction.OnPasswordVisibilityToggle -> {
                _state.update {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }
            is SignInAction.OnSignInWithIdentifierAndPassword -> {
                val identifier = _state.value.identifier
                val password = _state.value.password
                viewModelScope.launch { onSignInWithIdentifierAndPassword(identifier, password) }
            }
            is SignInAction.OnSignInWithGoogle -> {
                viewModelScope.launch { onSignInWithGoogle(action.token) }
            }
            else -> Unit
        }
    }

    private suspend fun onSignInWithIdentifierAndPassword(
        identifier: String,
        password: String
    ) {
        val response = httpClient.post(ApiRoutes.Auth.Login) {
            setBody(LoginRequest(identifier, password))
        }
        if(response.status.isSuccess()) {
            val body = response.body<LoginResponse>()
            _state.update { it.copy(identifier = body.name) }
            println(body)
        }
        else println(response.bodyAsText())
    }

    private suspend fun onSignInWithGoogle(idToken: String) {
        try {
            val response = httpClient.post(ApiRoutes.Auth.GoogleOauth) {
                setBody(GoogleLoginRequest(idToken))
            }

            if (response.status.isSuccess()) {
                val body = response.body<LoginResponse>()
                _state.update { it.copy(identifier = body.email) }
                println(body)
            } else println(response.bodyAsText())
        } catch (e: Exception) {
            Log.e("SignInViewModel", "onSignInWithGoogle: ${e.message}")
        }
    }
}

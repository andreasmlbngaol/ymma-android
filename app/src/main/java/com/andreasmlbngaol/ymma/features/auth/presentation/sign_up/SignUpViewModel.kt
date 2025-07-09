package com.andreasmlbngaol.ymma.features.auth.presentation.sign_up

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreasmlbngaol.ymma.core.data.ApiRoutes
import com.andreasmlbngaol.ymma.domains.auth.GoogleLoginRequest
import com.andreasmlbngaol.ymma.domains.auth.LoginResponse
import com.andreasmlbngaol.ymma.domains.auth.RegisterRequest
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

class SignUpViewModel(
    private val httpClient: HttpClient
): ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun onAction(action: SignUpAction) {
        when(action) {
            is SignUpAction.OnNameChange -> {
                _state.value = _state.value.copy(name = action.name)
            }

            is SignUpAction.OnEmailChange -> {
                _state.value = _state.value.copy(email = action.email.trim())
            }

            is SignUpAction.OnPasswordChange -> {
                _state.value = _state.value.copy(password = action.password.trim())
            }

            is SignUpAction.OnPasswordVisibilityToggle -> {
                _state.value = _state.value.copy(passwordVisibility = !_state.value.passwordVisibility)
            }

            is SignUpAction.OnConfirmPasswordChange -> {
                _state.value = _state.value.copy(confirmPassword = action.confirmPassword)
            }

            is SignUpAction.OnSignUp -> {
                val name = _state.value.name.trim()
                val email = _state.value.email
                val password = _state.value.password

                viewModelScope.launch {
                    val response = httpClient.post(ApiRoutes.Auth.Register) {
                        setBody(
                            RegisterRequest(
                                name = name,
                                email = email,
                                password = password
                            )
                        )
                    }

                    if(response.status.isSuccess()) {
                        val body = response.body<LoginResponse>()
                        println(body)
                    }
                }
            }

            is SignUpAction.OnSignUpWithGoogle -> {
                viewModelScope.launch {
                    try {
                        val response = httpClient.post(ApiRoutes.Auth.GoogleOauth) {
                            setBody(GoogleLoginRequest(action.token))
                        }

                        if (response.status.isSuccess()) {
                            val body = response.body<LoginResponse>()
                            _state.update { it.copy(email = body.email, name = body.name) }
                            println(body)
                        } else println(response.bodyAsText())
                    } catch (e: Exception) {
                        Log.e("SignInViewModel", "onSignInWithGoogle: ${e.message}")
                    }
                }
            }

            else -> Unit
        }

    }

}
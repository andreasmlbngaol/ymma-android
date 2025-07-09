package com.andreasmlbngaol.ymma.features.auth.presentation.sign_in

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andreasmlbngaol.ymma.BuildConfig
import com.andreasmlbngaol.ymma.features.auth.presentation.components.GoogleAuthButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToResetPassword: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SignInScreenRoot(
        state = state,
        onAction = { action ->
            when(action) {
                is SignInAction.OnNavigateBack -> onNavigateBack()
                is SignInAction.OnNavigateToSignUp -> onNavigateToSignUp()
                is SignInAction.OnNavigateToResetPassword -> onNavigateToResetPassword()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignInScreenRoot(
    state: SignInState,
    onAction: (SignInAction) -> Unit,
) {
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Masok",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(Modifier.height(32.dp))
                OutlinedTextField(
                    value = state.identifier,
                    onValueChange = { onAction(SignInAction.OnIdentifierChange(it)) },
                    label = { Text("Email/Username") },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        }
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onAction(SignInAction.OnPasswordChange(it)) },
                    label = { Text("Password") },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = null
                        )
                    },
                    visualTransformation = if (!state.isPasswordVisible) PasswordVisualTransformation(
                        mask = '*'
                    ) else VisualTransformation.None,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onAction(SignInAction.OnPasswordVisibilityToggle)
                            },
                            shapes = IconButtonDefaults.shapes()
                        ) {
                            AnimatedContent(state.isPasswordVisible) {
                                Icon(
                                    imageVector = if (!it) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onAction(SignInAction.OnSignInWithIdentifierAndPassword)
                        }
                    )
                )
                TextButton(
                    onClick = { onAction(SignInAction.OnNavigateToResetPassword) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Pikun? Lupa Password?",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Button(
                    onClick = { onAction(SignInAction.OnSignInWithIdentifierAndPassword) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Masok")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        "Atau",
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                GoogleAuthButton(BuildConfig.GOOGLE_WEB_CLIENT_ID) { token ->
                    onAction(SignInAction.OnSignInWithGoogle(token))
                }

                TextButton(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { onAction(SignInAction.OnNavigateToSignUp) }
                ) {
                    Text(
                        text = "Belum ada akun klen? Daftarlah dulu!"
                    )
                }
            }
        }
    }
}
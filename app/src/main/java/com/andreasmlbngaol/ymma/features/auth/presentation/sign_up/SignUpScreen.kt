package com.andreasmlbngaol.ymma.features.auth.presentation.sign_up

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andreasmlbngaol.ymma.BuildConfig
import com.andreasmlbngaol.ymma.features.auth.presentation.components.GoogleAuthButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinViewModel(),
    onNavigateToSignIn: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SignUpScreenRoot(
        state = state,
        onAction = { action ->
            when(action) {
                is SignUpAction.OnNavigateToSignIn -> onNavigateToSignIn()
                is SignUpAction.OnNavigateBack -> onNavigateBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignUpScreenRoot(
    state: SignUpState,
    onAction: (SignUpAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
//            IconButton(
//                onClick = { onAction(SignUpAction.OnNavigateBack) }
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "Back"
//                )
//            }
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Daftar",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(Modifier.height(32.dp))
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onAction(SignUpAction.OnNameChange(it)) },
                    label = { Text("Nama") },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.PermIdentity,
                            contentDescription = null
                        )
                    }
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onAction(SignUpAction.OnEmailChange(it)) },
                    label = { Text("Email") },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null
                        )
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onAction(SignUpAction.OnPasswordChange(it)) },
                        label = {
                            Text(
                                text = "Password",
                                overflow = TextOverflow.Clip
                            )
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.weight(1f),
                        visualTransformation = if (!state.passwordVisibility) PasswordVisualTransformation(
                            '*'
                        ) else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(
                                onClick = { onAction(SignUpAction.OnPasswordVisibilityToggle) },
                                shapes = IconButtonDefaults.shapes()
                            ) {
                                AnimatedContent(state.passwordVisibility) {
                                    Icon(
                                        imageVector = if (!it) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { onAction(SignUpAction.OnConfirmPasswordChange(it)) },
                        label = {
                            Text(
                                text = "Password Lagi",
                                overflow = TextOverflow.Clip
                            )
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        visualTransformation = if (!state.passwordVisibility) PasswordVisualTransformation(
                            '*'
                        ) else VisualTransformation.None
                    )
                }

                Button(
                    onClick = { onAction(SignUpAction.OnSignUp) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.password == state.confirmPassword && state.password.isNotEmpty()
                ) {
                    Text("Daftar")
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
                    onAction(SignUpAction.OnSignUpWithGoogle(token))
                }

                TextButton(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { onAction(SignUpAction.OnNavigateToSignIn) }
                ) {
                    Text(
                        text = "Udah adanya akunku. Masok ajalah!"
                    )
                }
            }
        }
    }
}
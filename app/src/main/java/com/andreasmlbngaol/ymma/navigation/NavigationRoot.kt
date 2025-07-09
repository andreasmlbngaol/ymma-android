package com.andreasmlbngaol.ymma.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.andreasmlbngaol.ymma.features.auth.presentation.landing_page.LandingPageScreen
import com.andreasmlbngaol.ymma.features.auth.presentation.sign_in.SignInScreen
import com.andreasmlbngaol.ymma.features.auth.presentation.sign_up.SignUpScreen

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val backstack = rememberNavBackStack(LandingPageRoute)

    fun onBack() { backstack.removeLastOrNull() }

    NavDisplay(
        modifier = modifier,
        backStack = backstack,
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
            rememberSceneSetupNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<LandingPageRoute> {
                LandingPageScreen(
                    onNavigateToSignIn = {
                        backstack.add(SignInRoute)
                    },
                    onNavigateToSignUp = {
                        backstack.add(SignUpRoute)
                    }
                )
            }
            entry<SignInRoute>(
                metadata = if(backstack.elementAtOrNull(backstack.lastIndex - 1) == SignUpRoute) {
                    slideInFromLeftAndOutFromTop()
                } else {
                    slideInFromBottomAndOutFromTop()
                }
            ) {
                LaunchedEffect(Unit) { backstack.remove(SignUpRoute) }
                SignInScreen(
                    onNavigateBack = { onBack() },
                    onNavigateToSignUp = {
                        backstack.add(SignUpRoute)
                    },
                    onNavigateToResetPassword = {
                        backstack.add(ForgotPasswordRoute)
                    }
                )
            }
            entry<SignUpRoute>(
                metadata = if(backstack.elementAtOrNull(backstack.lastIndex - 1) == SignInRoute) {
                    slideInFromRightAndOutFromTop()
                } else {
                    slideInFromBottomAndOutFromTop()
                }
            ) {
                LaunchedEffect(Unit) { backstack.remove(SignInRoute) }
                SignUpScreen(
                    onNavigateBack = { onBack() },
                    onNavigateToSignIn = {
                        backstack.add(SignInRoute)
                    }
                )
            }
        }
    )
}
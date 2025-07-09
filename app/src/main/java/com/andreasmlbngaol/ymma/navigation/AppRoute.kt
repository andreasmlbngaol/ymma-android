@file:Suppress("unused")

package com.andreasmlbngaol.ymma.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object LandingPageRoute: NavKey

@Serializable
data object SignInRoute: NavKey

@Serializable
data object SignUpRoute: NavKey

@Serializable
data object ForgotPasswordRoute: NavKey

@Serializable
data object EmailSentRoute: NavKey

@Serializable
data class TestRoute(val id: Int): NavKey
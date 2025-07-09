@file:Suppress("ConstPropertyName")

package com.andreasmlbngaol.ymma.core.data

import com.andreasmlbngaol.ymma.BuildConfig

object ApiRoutes {
    private const val BASE_URL = BuildConfig.BASE_URL

    object Auth {
        const val Login = "$BASE_URL/api/auth/login"
        const val Register = "$BASE_URL/api/auth/register"
        const val GoogleOauth = "$BASE_URL/api/auth/google-oauth"
    }
}
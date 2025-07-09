package com.andreasmlbngaol.ymma.di

import com.andreasmlbngaol.ymma.core.data.HttpClientFactory
import com.andreasmlbngaol.ymma.features.auth.presentation.sign_in.SignInViewModel
import com.andreasmlbngaol.ymma.features.auth.presentation.sign_up.SignUpViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(modules)
    }
}

val modules = module {
    single<HttpClientEngine> { CIO.create() }
    single { HttpClientFactory.create(get()) }

    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
}


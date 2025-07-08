package com.andreasmlbngaol.ymma.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
val HTTP_CLIENT = HttpClient(CIO) {
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
            }
        )
    }
}
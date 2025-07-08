package com.andreasmlbngaol.ymma.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import com.andreasmlbngaol.ymma.utils.respondJson

fun Application.statusPagesPlugin() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respondJson(HttpStatusCode.BadRequest, "Invalid Request Body")
        }
        exception<Throwable> { call, cause ->
            call.respondJson(HttpStatusCode.InternalServerError, cause::class.qualifiedName ?: "Unknown error")
        }
    }
}
package com.andreasmlbngaol.ymma

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import com.andreasmlbngaol.ymma.controllers.auth.JwtConfig
import com.andreasmlbngaol.ymma.controllers.auth.authRoute
import com.andreasmlbngaol.ymma.controllers.comment.commentRoute
import com.andreasmlbngaol.ymma.controllers.course.courseRoute
import com.andreasmlbngaol.ymma.controllers.post.postRoute
import com.andreasmlbngaol.ymma.database.DatabaseFactory
import com.andreasmlbngaol.ymma.plugins.authenticationPlugin
import com.andreasmlbngaol.ymma.plugins.contentNegotiationPlugin
import com.andreasmlbngaol.ymma.plugins.corsPlugin
import com.andreasmlbngaol.ymma.plugins.statusPagesPlugin
import com.andreasmlbngaol.ymma.utils.HTTP_CLIENT
import com.andreasmlbngaol.ymma.utils.respondJson

fun main(args: Array<String>): Unit = EngineMain.main(args)

@OptIn(ExperimentalSerializationApi::class)
fun Application.module() {
    initKtor(environment.config)

    corsPlugin()
    contentNegotiationPlugin()
    authenticationPlugin(HTTP_CLIENT)
    statusPagesPlugin()

    routing {
        route("/") {
            get {
                call.respondRedirect("/api")
            }
        }


        route("/api") {
            get { call.respondJson(HttpStatusCode.OK, "Allo Woldeu") }

            authRoute(HTTP_CLIENT)
            courseRoute()
            postRoute()
            commentRoute()
        }
    }
}

fun initKtor(
    config: ApplicationConfig
) {
    JwtConfig.init(config)
    DatabaseFactory.init(config)
}
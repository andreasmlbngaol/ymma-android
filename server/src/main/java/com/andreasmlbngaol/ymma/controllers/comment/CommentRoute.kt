package com.andreasmlbngaol.ymma.controllers.comment

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.andreasmlbngaol.ymma.controllers.auth.userId
import com.andreasmlbngaol.ymma.database.dao.CommentsDao
import com.andreasmlbngaol.ymma.domains.comment.CreateCommentRequest
import com.andreasmlbngaol.ymma.plugins.AuthNames.JWT_AUTH
import com.andreasmlbngaol.ymma.utils.respondJson

fun Route.commentRoute() {
    authenticate(JWT_AUTH) {
        route("posts/{postId}/comments") {
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.userId()

                val postId = call.parameters["postId"]?.toLongOrNull()
                    ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid post ID")

                val parentId = call.queryParameters["parent_id"]?.toLongOrNull()
                val payload = call.receive<CreateCommentRequest>()
                val content = payload.content.trim()

                if (content.isBlank()) {
                    return@post call.respondJson(HttpStatusCode.BadRequest, "Comment content cannot be empty")
                }

                if (content.length > 1000) {
                    return@post call.respondJson(
                        HttpStatusCode.BadRequest,
                        "Comment content is too long. Maximum length is 1000 characters."
                    )
                }

                val commentId = CommentsDao.create(
                    postId = postId,
                    userId = userId,
                    content = content,
                    parentId = parentId
                )

                call.respondJson(
                    HttpStatusCode.Created,
                    "Comment $commentId with parent $parentId created successfully",
                )
            }

            get {
                val postId = call.parameters["postId"]?.toLongOrNull()
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid post ID")

                val comments = CommentsDao.findAllByPostId(postId)

                call.respond(
                    HttpStatusCode.OK,
                    comments
                )
            }
        }
    }
}
package com.andreasmlbngaol.ymma.controllers.post

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.andreasmlbngaol.ymma.controllers.auth.userId
import com.andreasmlbngaol.ymma.database.dao.PostAttachmentsDao
import com.andreasmlbngaol.ymma.database.dao.PostsDao
import com.andreasmlbngaol.ymma.domains.post.GetPostResponse
import com.andreasmlbngaol.ymma.plugins.AuthNames.JWT_AUTH
import com.andreasmlbngaol.ymma.utils.respondJson
import java.io.File

@Suppress("DEPRECATION")
fun Route.postRoute() {
    authenticate(JWT_AUTH) {
        route("/courses/{courseId}/posts") {
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.userId()

                val courseId = call.parameters["courseId"]?.toLongOrNull()
                    ?: return@post call.respondJson(
                        HttpStatusCode.BadRequest,
                        "Invalid course ID"
                    )

                withContext(Dispatchers.IO) {
                    val multipart = call.receiveMultipart()
                    var content: String? = null
                    val attachments = mutableListOf<BufferedAttachment>()

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                if (part.name == "content") {
                                    content = part.value.trim()
                                }
                            }

                            is PartData.FileItem -> {
                                if (part.name == "attachment") {
                                    val originalFileName = part.originalFileName?.trim() ?: "unknown"
                                    val bytes = part.streamProvider().readBytes()
                                    val contentType = part.contentType?.toString()
                                    attachments.add(BufferedAttachment(originalFileName, bytes, contentType))
                                }
                            }

                            else -> {}
                        }
                        part.dispose()
                    }

                    if (content.isNullOrBlank()) {
                        return@withContext call.respondJson(
                            HttpStatusCode.BadRequest,
                            "Post content cannot be empty"
                        )
                    }

                    val postId = PostsDao.create(courseId, userId, content)

                    attachments.forEachIndexed { index, attachment ->
                        val fileDir = "uploads/posts"
                        File(fileDir).mkdirs()

                        val ext = File(attachment.originalFileName).extension
                        val fileName = "post-$postId-attachment-$index.$ext"
                        val filePath = "$fileDir/$fileName"

                        File(filePath).writeBytes(attachment.bytes)

                        PostAttachmentsDao.create(
                            postId = postId.value,
                            fileName = fileName,
                            fileUrl = filePath,
                            mimeType = attachment.contentType ?: "application/octet-stream"
                        )
                    }

                    call.respondJson(
                        HttpStatusCode.Created,
                        "Post created successfully with ID $postId"
                    )
                }
            }

            get {
                val courseId = call.parameters["courseId"]?.toLongOrNull()
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid course ID")

                val posts = PostsDao.findAllByCourseId(courseId)

                val response = posts.map { post ->
                    val attachments = PostAttachmentsDao.findAllByPostId(post.id)
                    GetPostResponse(
                        id = post.id,
                        courseId = post.courseId,
                        authorId = post.authorId,
                        content = post.content,
                        type = post.type,
                        createdAt = post.createdAt,
                        attachments = attachments
                    )
                }

                call.respond(HttpStatusCode.OK, response)
            }

            get("/{postId}") {
                val courseId = call.parameters["courseId"]?.toLongOrNull()
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid course ID")

                val postId = call.parameters["postId"]?.toLongOrNull()
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Invalid post ID")

                val post = PostsDao.findById(postId)
                    ?: return@get call.respondJson(HttpStatusCode.NotFound, "Post not found")

                if(post.courseId != courseId) {
                    return@get call.respondJson(HttpStatusCode.BadRequest, "Course ID does not match post's course")
                }

                val attachments = PostAttachmentsDao.findAllByPostId(postId)

                val response = GetPostResponse(
                    id = post.id,
                    courseId = post.courseId,
                    authorId = post.authorId,
                    content = post.content,
                    type = post.type,
                    createdAt = post.createdAt,
                    attachments = attachments
                )

                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}
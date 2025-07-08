package com.andreasmlbngaol.ymma.domains.post

import kotlinx.serialization.Serializable
import com.andreasmlbngaol.ymma.type.PostType

@Serializable
data class GetPostResponse(
    val id: Long,
    val courseId: Long,
    val authorId: Long?,
    val content: String,
    val type: PostType,
    val attachments: List<PostAttachment> = emptyList(),
    val createdAt: String
)

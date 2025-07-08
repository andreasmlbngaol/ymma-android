package com.andreasmlbngaol.ymma.domains.comment

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val content: String,
    val parentId: Long? = null,
    val createdAt: String,
    val updatedAt: String
)

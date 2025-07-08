package com.andreasmlbngaol.ymma.domains.post

import com.andreasmlbngaol.ymma.type.PostType

data class Post(
    val id: Long,
    val courseId: Long,
    val authorId: Long? = null,
    val content: String,
    val type: PostType,
    val createdAt: String
)

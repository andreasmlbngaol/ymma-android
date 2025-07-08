package com.andreasmlbngaol.ymma.domains.post

import kotlinx.serialization.Serializable

@Serializable
data class PostAttachment(
    val id: Long,
    val postId: Long,
    val fileName: String,
    val fileUrl: String,
    val mimeType: String? = null,
    val uploadedAt: String
)
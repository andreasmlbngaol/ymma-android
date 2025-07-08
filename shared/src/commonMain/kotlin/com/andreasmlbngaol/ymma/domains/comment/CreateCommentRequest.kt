package com.andreasmlbngaol.ymma.domains.comment

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val content: String
)
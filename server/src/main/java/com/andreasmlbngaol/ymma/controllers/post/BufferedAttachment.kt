package com.andreasmlbngaol.ymma.controllers.post

@Suppress("ArrayInDataClass")
data class BufferedAttachment(
    val originalFileName: String,
    val bytes: ByteArray,
    val contentType: String?
)

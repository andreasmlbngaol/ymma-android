package com.andreasmlbngaol.ymma.database.converter

import com.andreasmlbngaol.ymma.database.tables.PostAttachments
import com.andreasmlbngaol.ymma.domains.post.PostAttachment
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toPostAttachment(): PostAttachment {
    return PostAttachment(
        id = this[PostAttachments.id].value,
        postId = this[PostAttachments.post].value,
        fileName = this[PostAttachments.fileName],
        fileUrl = this[PostAttachments.fileUrl],
        mimeType = this[PostAttachments.mimeType],
        uploadedAt = this[PostAttachments.uploadedAt].toString()
    )
}
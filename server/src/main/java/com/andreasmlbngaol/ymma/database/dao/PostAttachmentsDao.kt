package com.andreasmlbngaol.ymma.database.dao

import com.andreasmlbngaol.ymma.database.converter.toPostAttachment
import com.andreasmlbngaol.ymma.database.tables.PostAttachments
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object PostAttachmentsDao {
    fun create(
        postId: Long,
        fileName: String,
        fileUrl: String,
        mimeType: String,
    ) = transaction {
        PostAttachments.insertAndGetId {
            it[PostAttachments.post] = postId
            it[PostAttachments.fileName] = fileName
            it[PostAttachments.fileUrl] = fileUrl
            it[PostAttachments.mimeType] = mimeType
        }
    }

    fun findAllByPostId(postId: Long) = transaction {
        PostAttachments
            .selectAll()
            .where { PostAttachments.post eq postId }
            .map { it.toPostAttachment() }
    }
}
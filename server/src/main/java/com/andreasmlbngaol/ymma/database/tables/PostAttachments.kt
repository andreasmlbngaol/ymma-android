package com.andreasmlbngaol.ymma.database.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object PostAttachments: LongIdTable("post_attachments") {
    val post = reference("post_id", Posts)
    val fileName = varchar("file_name", 255)
    val fileUrl = varchar("file_url", 255)
    val mimeType = varchar("mime_type", 64)
    val uploadedAt = datetime("uploaded_at").defaultExpression(CurrentDateTime)
}
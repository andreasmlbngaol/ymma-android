package com.andreasmlbngaol.ymma.database.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Notifications: LongIdTable("notifications") {
    val user = optReference("user_id", Users)
    val content = varchar("content", 255)
    val isRead = bool("is_read").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
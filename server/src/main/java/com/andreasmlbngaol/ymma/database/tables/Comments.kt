package com.andreasmlbngaol.ymma.database.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Comments: LongIdTable("comments") {
    val post = reference("post_id", Posts)
    val author = reference("author_id", Users)
    val content = text("content")
    val parent = optReference("parent_id", Comments)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    val isActive = bool("is_active").default(true)
}
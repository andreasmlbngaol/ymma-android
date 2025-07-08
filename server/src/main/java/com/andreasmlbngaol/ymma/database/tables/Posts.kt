package com.andreasmlbngaol.ymma.database.tables

import com.andreasmlbngaol.ymma.type.PostType
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Posts: LongIdTable("posts") {
    val course = reference("course_id", Courses)
    val author = optReference("author_id", Users)
    val content = text("content")
    val type = enumerationByName<PostType>("type", 20).default(PostType.System)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}
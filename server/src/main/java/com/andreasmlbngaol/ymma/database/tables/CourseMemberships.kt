package com.andreasmlbngaol.ymma.database.tables

import com.andreasmlbngaol.ymma.type.Role
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object CourseMemberships: LongIdTable("course_memberships") {
    val course = reference("course_id", Courses)
    val user = reference("user_id", Users)
    val role = enumerationByName<Role>("role", 20)
    val joinedAt = datetime("joined_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex("user_course_unique", user, course)
    }
}
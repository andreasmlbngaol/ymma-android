package com.andreasmlbngaol.ymma.database.dao

import com.andreasmlbngaol.ymma.database.tables.CourseMemberships
import com.andreasmlbngaol.ymma.type.Role
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object CourseMembershipsDao {
    fun create(
        courseId: Long,
        userId: Long,
        role: Role
    ): Long = transaction {
        CourseMemberships.insertAndGetId {
            it[CourseMemberships.course] = courseId
            it[CourseMemberships.user] = userId
            it[CourseMemberships.role] = role
        }.value
    }

    fun existByUserIdAndCourseId(
        userId: Long,
        courseId: Long
    ) = transaction {
        CourseMemberships
            .selectAll()
            .where { (CourseMemberships.user eq userId) and (CourseMemberships.course eq courseId) }
            .count() > 0
    }

    fun findRole(
        courseId: Long,
        userId: Long
    ) = transaction {
        CourseMemberships
            .select(CourseMemberships.role)
            .where { (CourseMemberships.user eq userId) and (CourseMemberships.course eq courseId) }
            .map { it[CourseMemberships.role] }
            .singleOrNull()
    }
}
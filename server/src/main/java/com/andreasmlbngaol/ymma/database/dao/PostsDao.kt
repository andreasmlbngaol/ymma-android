package com.andreasmlbngaol.ymma.database.dao

import com.andreasmlbngaol.ymma.database.converter.toPost
import com.andreasmlbngaol.ymma.database.tables.Posts
import com.andreasmlbngaol.ymma.type.PostType
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object PostsDao {
    fun create(
        courseId: Long,
        userId: Long,
        content: String,
        type: PostType = PostType.User
    ) = transaction {
        Posts.insertAndGetId {
            it[Posts.course] = courseId
            it[Posts.author] = userId
            it[Posts.content] = content
            it[Posts.type] = type
        }
    }

    fun findById(postId: Long) = transaction {
        Posts
            .selectAll()
            .where { Posts.id eq postId }
            .map { it.toPost() }
            .singleOrNull()
    }

    fun findAllByCourseId(courseId: Long) = transaction {
        Posts
            .selectAll()
            .where { Posts.course eq courseId }
            .map { it.toPost() }
    }
}
package com.andreasmlbngaol.ymma.database.dao

import com.andreasmlbngaol.ymma.database.converter.toComment
import com.andreasmlbngaol.ymma.database.tables.Comments
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object CommentsDao {
    fun create(
        postId: Long,
        userId: Long,
        content: String,
        parentId: Long?
    ) = transaction {
        Comments.insertAndGetId {
            it[Comments.post] = postId
            it[Comments.author] = userId
            it[Comments.content] = content
            it[Comments.parent] = parentId
            it[Comments.isActive] = true
        }
    }

    fun findAllByPostId(postId: Long) = transaction {
        Comments
            .selectAll()
            .where { Comments.post eq postId }
            .map { it.toComment() } // Assuming a toComment() extension function exists
    }
}
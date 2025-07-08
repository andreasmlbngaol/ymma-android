package com.andreasmlbngaol.ymma.database.dao

import com.andreasmlbngaol.ymma.database.converter.toUser
import com.andreasmlbngaol.ymma.database.tables.Users
import com.andreasmlbngaol.ymma.type.Provider
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

object UsersDao {
    fun findById(id: Long) = transaction {
        Users
            .selectAll()
            .where { Users.id eq id }
            .mapNotNull { it.toUser() }
            .singleOrNull()
    }

    fun findByEmail(email: String) = transaction {
        Users
            .selectAll()
            .where { Users.email eq email }
            .mapNotNull { it.toUser() }
            .singleOrNull()
    }

    fun findByUsername(username: String) = transaction {
        Users
            .selectAll()
            .where { Users.username eq username }
            .mapNotNull { it.toUser() }
            .singleOrNull()
    }

    fun findByProviderAndSub(
        provider: Provider = Provider.Google,
        sub: String
    ) = transaction {
        Users
            .selectAll()
            .where {
                (Users.oAuthProvider eq provider) and (Users.oAuthSub eq sub)
            }
            .mapNotNull { it.toUser() }
            .singleOrNull()
    }

    fun updateOAuth(
        id: Long,
        oAuthProvider: Provider,
        oAuthSub: String
    ) = transaction {
        Users
            .update(
                { Users.id eq id }
            ) {
                it[Users.isVerified] = true
                it[Users.oAuthProvider] = oAuthProvider
                it[Users.oAuthSub] = oAuthSub
            }
    }

    fun existByEmail(email: String) = transaction {
        Users
            .select(Users.id)
            .where { Users.email eq email }
            .count() > 0
    }

    fun existByUsername(username: String) = transaction {
        Users
            .select(Users.id)
            .where { Users.username eq username }
            .count() > 0
    }


    fun create(
        name: String,
        email: String,
        username: String? = null,
        passwordHashed: String? = null,
        isVerified: Boolean = false,
        imageUrl: String? = null,
        oAuthProvider: Provider? = null,
        oAuthSub: String? = null
    ) = transaction {
        Users.insertAndGetId {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.username] = username
            it[Users.passwordHashed] = passwordHashed
            it[Users.isVerified] = isVerified
            it[Users.imageUrl] = imageUrl
            it[Users.oAuthProvider] = oAuthProvider
            it[Users.oAuthSub] = oAuthSub
        }
    }

    fun changePassword(
        userId: Long,
        passwordHashed: String
    ) = transaction {
        Users
            .update(
                { Users.id eq userId }
            ) {
                it[Users.passwordHashed] = passwordHashed
            }
    }
}
package com.andreasmlbngaol.ymma.database.tables

import com.andreasmlbngaol.ymma.type.Provider
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Users: LongIdTable("users") {
    val name = varchar("name", 40)
    val email = varchar("email", 60).uniqueIndex()
    val username = varchar("username", 32).uniqueIndex().nullable()
    val passwordHashed = varchar("password_hashed", 255).nullable()
    val isVerified = bool("is_verified").default(false)
    val imageUrl = varchar("image_url", 255).nullable()
    val isActive = bool("is_active").default(true)

    val oAuthProvider = enumerationByName<Provider>("oauth_provider", 16).nullable()
    val oAuthSub = varchar("oauth_sub", 255).nullable()
}
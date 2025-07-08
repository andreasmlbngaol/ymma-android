package com.andreasmlbngaol.ymma.database.converter

import com.andreasmlbngaol.ymma.database.tables.Users
import com.andreasmlbngaol.ymma.domains.auth.User
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toUser() = User(
    id = this[Users.id].value,
    name = this[Users.name],
    email = this[Users.email],
    username = this[Users.username],
    passwordHashed = this[Users.passwordHashed],
    isVerified = this[Users.isVerified],
    imageUrl = this[Users.imageUrl],
    isActive = this[Users.isActive]
)
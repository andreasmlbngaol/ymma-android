package com.andreasmlbngaol.ymma.utils

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

open class SoftDeletedLongIdTable(
    name: String = ""
): LongIdTable(name = name)
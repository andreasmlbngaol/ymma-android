package com.andreasmlbngaol.ymma.domains.course

import kotlinx.serialization.Serializable

@Serializable
data class EnrollCourseRequest(
    val code: String
)

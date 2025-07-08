package com.andreasmlbngaol.ymma.domains.course

import kotlinx.serialization.Serializable

@Serializable
data class CreateCourseResponse(
    val id: Long,
    val collegeName: String? = null,
    val departmentName: String? = null,
    val code: String,
    val name: String,
    val abbreviation: String? = null,
    val location: String? = null,
    val lecturerName: String? = null,
    val year: Int? = null
)

package com.andreasmlbngaol.ymma.domains.course

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCourseRequest(
    val collegeName: String? = null,
    val departmentName: String? = null,
    val name: String? = null,
    val abbreviation: String? = null,
    val location: String? = null,
    val lecturerName: String? = null,
    val year: Int? = null
)
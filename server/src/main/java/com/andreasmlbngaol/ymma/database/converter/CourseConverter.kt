package com.andreasmlbngaol.ymma.database.converter

import com.andreasmlbngaol.ymma.database.tables.Courses
import com.andreasmlbngaol.ymma.domains.course.Course
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toCourse() = Course(
    id = this[Courses.id].value,
    collegeName = this[Courses.collegeName],
    departmentName = this[Courses.departmentName],
    code = this[Courses.code],
    name = this[Courses.name],
    abbreviation = this[Courses.abbreviation],
    location = this[Courses.location],
    lecturerName = this[Courses.lecturerName],
    year = this[Courses.year],
    createdAt = this[Courses.createdAt].toString()
)
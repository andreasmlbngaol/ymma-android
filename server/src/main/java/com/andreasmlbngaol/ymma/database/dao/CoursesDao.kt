package com.andreasmlbngaol.ymma.database.dao

import com.andreasmlbngaol.ymma.database.converter.toCourse
import com.andreasmlbngaol.ymma.database.tables.Courses
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

object CoursesDao {
    fun create(
        collegeName: String?,
        departmentName: String?,
        code: String,
        name: String,
        abbreviation: String?,
        location: String?,
        lecturerName: String?,
        year: Int?
    ): Long = transaction {
        Courses.insertAndGetId {
            it[Courses.collegeName] = collegeName
            it[Courses.departmentName] = departmentName
            it[Courses.code] = code
            it[Courses.name] = name
            it[Courses.abbreviation] = abbreviation
            it[Courses.location] = location
            it[Courses.lecturerName] = lecturerName
            it[Courses.year] = year
        }.value
    }

    fun existByCode(code: String) = transaction {
        Courses
            .select(Courses.id)
            .where { Courses.code eq code }
            .count() > 0
    }

    fun findByCode(code: String) = transaction {
        Courses
            .selectAll()
            .where { Courses.code eq code }
            .map { it.toCourse() }
            .singleOrNull()
    }

    fun findById(id: Long) = transaction {
        Courses
            .selectAll()
            .where { Courses.id eq id }
            .map { it.toCourse() }
            .singleOrNull()
    }

    fun update(
        id: Long,
        collegeName: String?,
        departmentName: String?,
        code: String,
        name: String,
        abbreviation: String?,
        location: String?,
        lecturerName: String?,
        year: Int?
    ) = transaction {
        Courses.update({ Courses.id eq id }) {
            it[Courses.collegeName] = collegeName
            it[Courses.departmentName] = departmentName
            it[Courses.code] = code
            it[Courses.name] = name
            it[Courses.abbreviation] = abbreviation
            it[Courses.location] = location
            it[Courses.lecturerName] = lecturerName
            it[Courses.year] = year
        }
    }
}
package com.andreasmlbngaol.ymma.database.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Courses: LongIdTable("courses") {
    val collegeName = varchar("college_name", 255).nullable()
    val departmentName = varchar("department_name", 255).nullable()
    val code = char("code", 6).uniqueIndex()
    val name = varchar("name", 255)
    val location = varchar("location", 255).nullable()
    val lecturerName = varchar("lecturer_name", 128).nullable()
    val abbreviation = varchar("abbreviation", 64).nullable()
    val year = integer("year").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
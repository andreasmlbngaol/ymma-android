package com.andreasmlbngaol.ymma.database.tables

import com.andreasmlbngaol.ymma.type.CourseMode
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.time

object CourseScheduleOverrides: LongIdTable("course_schedule_overrides") {
    val course = reference("course_id", Courses)
    val originalDate = date("original_date")
    val newDate = date("new_date").nullable()
    val newStartTime = time("new_start_time").nullable()
    val newEndTime = time("new_end_time").nullable()
    val reason = varchar("reason", 255).nullable()
    val note = varchar("note", 255).nullable()
    val mode = enumerationByName<CourseMode>("mode", 20).default(CourseMode.Unknown)
}
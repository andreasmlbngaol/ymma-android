package com.andreasmlbngaol.ymma.database.tables

import com.andreasmlbngaol.ymma.type.Day
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.time

object CourseSchedules: LongIdTable("course_schedules") {
    val course = reference("course_id", Courses)
    val day = enumerationByName<Day>("day", 20).nullable()
    val startTime = time("start_time").nullable()
    val endTime = time("end_time").nullable()
    val startDate = date("start_date").nullable()
    val endDate = date("end_date").nullable()
}
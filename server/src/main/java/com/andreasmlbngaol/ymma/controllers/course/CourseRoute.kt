package com.andreasmlbngaol.ymma.controllers.course

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import com.andreasmlbngaol.ymma.controllers.auth.userId
import com.andreasmlbngaol.ymma.database.dao.CourseMembershipsDao
import com.andreasmlbngaol.ymma.database.dao.CoursesDao
import com.andreasmlbngaol.ymma.domains.course.CreateCourseRequest
import com.andreasmlbngaol.ymma.domains.course.CreateCourseResponse
import com.andreasmlbngaol.ymma.domains.course.EnrollCourseRequest
import com.andreasmlbngaol.ymma.domains.course.UpdateCourseRequest
import com.andreasmlbngaol.ymma.plugins.AuthNames.JWT_AUTH
import com.andreasmlbngaol.ymma.type.Role
import com.andreasmlbngaol.ymma.utils.respondJson

fun Route.courseRoute() {
    authenticate(JWT_AUTH) {
        route("/courses") {
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.userId()

                val payload = call.receive<CreateCourseRequest>()
                val name = payload.name.trim()
                if (name.isBlank()) {
                    return@post call.respondJson(
                        HttpStatusCode.BadRequest,
                        "Course name cannot be empty"
                    )
                }
                val collegeName = payload.collegeName?.trim()
                val departmentName = payload.departmentName?.trim()
                val abbreviation = payload.abbreviation?.trim()?.uppercase()
                val location = payload.location?.trim()
                val lecturerName = payload.lecturerName?.trim()
                val year = payload.year

                var code = generateCode()
                while (CoursesDao.existByCode(code)) {
                    code = generateCode()
                }

                val courseId = CoursesDao.create(
                    collegeName = collegeName,
                    departmentName = departmentName,
                    code = code,
                    name = name,
                    abbreviation = abbreviation,
                    location = location,
                    lecturerName = lecturerName,
                    year = year
                )

                if(CourseMembershipsDao.create(
                    userId = userId,
                    courseId = courseId,
                    role = Role.Master
                ) <= 0) {
                    return@post call.respondJson(
                        HttpStatusCode.InternalServerError,
                        "Failed to create course membership for user"
                    )
                }

                call.respond(
                    HttpStatusCode.Created,
                    CreateCourseResponse(
                        id = courseId,
                        collegeName = collegeName,
                        departmentName = departmentName,
                        code = code,
                        name = name,
                        abbreviation = abbreviation,
                        location = location,
                        lecturerName = lecturerName,
                        year = year,
                    )
                )
            }

            post("/enroll") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.userId()

                val payload = call.receive<EnrollCourseRequest>()
                val courseCode = payload.code.trim()

                val course = CoursesDao.findByCode(courseCode)
                    ?: return@post call.respondJson(
                        HttpStatusCode.NotFound,
                        "Course with code $courseCode does not exist"
                    )

                if(CourseMembershipsDao.existByUserIdAndCourseId(userId, course.id))
                    return@post call.respondJson(
                        HttpStatusCode.Conflict,
                        "You are already enrolled in course ${course.name}"
                    )

                if (CourseMembershipsDao.create(course.id, userId, Role.Member) <= 0) {
                    return@post call.respondJson(
                        HttpStatusCode.InternalServerError,
                        "Failed to enroll in course"
                    )
                }

                call.respondJson(HttpStatusCode.OK, "Successfully enrolled in course ${course.name}")
            }

            patch("/{courseId}") {
                val courseId = call.parameters["courseId"]?.toLongOrNull()
                    ?: return@patch call.respondJson(HttpStatusCode.BadRequest, "Invalid course ID")

                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.userId()

                if (!userId.isMasterOrStaffOf(courseId)) {
                    return@patch call.respondJson(
                        HttpStatusCode.Forbidden,
                        "You are not authorized to update this course"
                    )
                }

                val payload = call.receive<UpdateCourseRequest>()
                val collegeName = payload.collegeName?.trim()
                val departmentName = payload.departmentName?.trim()
                val name = payload.name?.trim()
                val abbreviation = payload.abbreviation?.trim()?.uppercase()
                val location = payload.location?.trim()
                val lecturerName = payload.lecturerName?.trim()
                val year = payload.year

                if (name != null && name.isBlank()) {
                    return@patch call.respondJson(HttpStatusCode.BadRequest, "Course name cannot be blank")
                }

                val course = CoursesDao.findById(courseId)
                    ?: return@patch call.respondJson(
                        HttpStatusCode.NotFound,
                        "Course with ID $courseId does not exist"
                    )

                val updatedCourse = CoursesDao.update(
                    id = courseId,
                    collegeName = collegeName ?: course.collegeName,
                    departmentName = departmentName ?: course.departmentName,
                    code = course.code,
                    name = name ?: course.name,
                    abbreviation = abbreviation ?: course.abbreviation,
                    location = location ?: course.location,
                    lecturerName = lecturerName ?: course.lecturerName,
                    year = year ?: course.year
                )

                if (updatedCourse < 1) {
                    return@patch call.respondJson(
                        HttpStatusCode.InternalServerError,
                        "Failed to update course"
                    )
                }

                call.respondJson(
                    HttpStatusCode.OK,
                    "Course updated successfully"
                )
            }
        }
    }
}

fun Long.isMasterOrStaffOf(courseId: Long) = CourseMembershipsDao
    .findRole(courseId, this) in setOf(Role.Master, Role.Staff)

fun generateCode(): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..6)
        .map { allowedChars.random() }
        .joinToString("")
}
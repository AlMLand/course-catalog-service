package com.AlMLand.entity

import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType.ALL

@Entity
@Table(name = "instructors")
data class Instructor(
    @EmbeddedId
    @field:Column(insertable = true, nullable = false, updatable = false)
    val instructorId: InstructorId,
    @OneToMany(mappedBy = "instructor", cascade = [ALL], orphanRemoval = true)
    val courses: MutableList<Course> = mutableListOf()
) {
    fun addCourse(course: Course) {
        courses.add(course)
        course.instructor = this
    }

    fun removeCourse(course: Course, otherInstructor: Instructor) {
        courses.remove(course)
        course.instructor = otherInstructor
    }

    override fun hashCode(): Int {
        return Objects.hash(instructorId)
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is Instructor) false
        else this.instructorId == other.instructorId
    }

    override fun toString(): String {
        return "Instructor(firstname: ${instructorId.firstName}, lastname: ${instructorId.lastName})"
    }
}
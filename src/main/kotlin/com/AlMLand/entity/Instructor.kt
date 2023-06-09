package com.AlMLand.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType.ALL

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "instructors")
data class Instructor @JvmOverloads constructor(
    @EmbeddedId
    @field:Column(insertable = true, nullable = false, updatable = false)
    val instructorId: InstructorId,

    @OneToMany(mappedBy = "instructor", cascade = [ALL], orphanRemoval = true)
    val courses: MutableList<Course> = mutableListOf()
) : AuditableEntity() {
    fun addCourse(course: Course) {
        courses.add(course)
        course.instructor = this
    }

    fun removeCourse(course: Course, otherInstructor: Instructor) {
        courses.remove(course)
        course.instructor = otherInstructor
    }

    override fun toString(): String {
        return """
            Instructor(firstname: ${instructorId.firstName}, lastname: ${instructorId.lastName}),
            created at: $createDate, created by: $createdBy,
            last modified at: $lastModifiedDate, last modified by: $lastModifiedBy
        """.trimIndent()
    }
}
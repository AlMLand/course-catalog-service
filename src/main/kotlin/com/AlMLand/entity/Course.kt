package com.AlMLand.entity

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "courses")
data class Course(
    @field:Column(insertable = true, nullable = false, updatable = true)
    var name: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_coursecategory",
        joinColumns = [JoinColumn(name = "course_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "course_category_id", referencedColumnName = "id")]
    )
    @field:Column(nullable = false, updatable = true)
    var categories: MutableList<CourseCategory>,

    @Id
    @GeneratedValue(generator = "CUSTOM_UUID")
    @GenericGenerator(name = "CUSTOM_UUID", strategy = "uuid2")
    @field:Column(insertable = true, nullable = false, updatable = false)
    val id: UUID?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(
            name = "instructor_firstname",
            referencedColumnName = "first_name",
            insertable = true,
            nullable = false,
            updatable = false
        ),
        JoinColumn(
            name = "instructor_lastname",
            referencedColumnName = "last_name",
            insertable = true,
            nullable = false,
            updatable = false
        )
    )
    var instructor: Instructor
) {
    override fun toString(): String {
        return "Course(name: $name, category: $categories, id: $id, instructor: ${instructor.instructorId})"
    }
}

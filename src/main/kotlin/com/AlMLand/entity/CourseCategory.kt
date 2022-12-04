package com.AlMLand.entity

import com.AlMLand.dto.enums.Category
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "course_categories")
data class CourseCategory(
    @field:Column(insertable = true, nullable = false, updatable = true)
    var category: Category,

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @field:Column(insertable = true, nullable = false, updatable = false, length = 36)
    val id: UUID?,

    @field:Column(insertable = true, nullable = true, updatable = true)
    var description: String? = null,

    @ManyToMany(mappedBy = "categories")
    val courses: MutableList<Course> = mutableListOf()
) {
    fun addCourse(course: Course) {
        courses.add(course)
        course.categories.add(this)
    }

    fun removeCourse(course: Course) {
        courses.remove(course)
        course.categories.remove(this)
    }
}

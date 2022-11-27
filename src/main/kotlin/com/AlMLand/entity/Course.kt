package com.AlMLand.entity

import javax.persistence.*

@Entity
@Table(name = "courses")
data class Course(
    @field:Column(nullable = false)
    var name: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_coursecategory",
        joinColumns = [JoinColumn(name = "course_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "course_category_id", referencedColumnName = "id")]
    )
    @field:Column(nullable = false)
    var category: List<CourseCategory>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var instructor: Instructor
) {
    override fun toString(): String {
        return "Course(name: $name, category: $category, id: $id, instructor: ${instructor.id})"
    }
}

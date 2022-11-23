package com.AlMLand.entity

import javax.persistence.*

@Entity
@Table(name = "courses")
data class Course(
    @field:Column(nullable = false)
    var name: String,

    @field:Column(nullable = false)
    var category: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    var instructor: Instructor
) {
    override fun toString(): String {
        return "Course(name: $name, category: $category, id: $id, instructor: ${instructor.id})"
    }
}

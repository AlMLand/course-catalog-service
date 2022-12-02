package com.AlMLand.entity.view

import org.hibernate.annotations.Immutable
import javax.persistence.*

@Entity
@Immutable
@Table(name = "courses_view")
data class CourseView(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(nullable = false, updatable = false)
    val id: Int,
    @field:Column(nullable = false, updatable = true)
    var name: String,
    @field:Column(name = "instructor_firstname", nullable = false, updatable = false)
    val firstName: String,
    @field:Column(name = "instructor_lastname", nullable = false, updatable = false)
    val lastName: String
)

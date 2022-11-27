package com.AlMLand.entity

import com.AlMLand.dto.enums.Category
import javax.persistence.*

@Entity
@Table(name = "course_categories")
data class CourseCategory(
    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    var category: Category,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,
    var description: String? = null
)

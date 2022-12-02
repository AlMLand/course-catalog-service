package com.AlMLand.entity.view

import com.AlMLand.dto.enums.Category
import org.hibernate.annotations.Immutable
import javax.persistence.*

@Entity
@Immutable
@Table(name = "course_categories_view")
data class CourseCategoryView(
    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false, updatable = true)
    var category: Category,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(nullable = false, updatable = false)
    val id: Int,

    @field:Column(nullable = true, updatable = true)
    var description: String? = null
)

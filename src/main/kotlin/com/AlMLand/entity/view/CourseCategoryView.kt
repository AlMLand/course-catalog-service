package com.AlMLand.entity.view

import com.AlMLand.dto.enums.Category
import org.hibernate.annotations.Immutable
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Immutable
@Table(name = "course_categories_view")
data class CourseCategoryView(
    @field:Column(nullable = false, updatable = true)
    var category: Category,

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @field:Column(insertable = true, nullable = false, updatable = false, length = 36)
    val id: UUID,

    @field:Column(nullable = true, updatable = true)
    var description: String? = null
)

package com.AlMLand.dto

import com.AlMLand.dto.enums.Category
import javax.validation.constraints.NotNull

data class CourseCategoryDTO(
    @field:NotNull(message = "CourseCategory.category must be null")
    var category: Category,
    val id: Int?,
    var description: String? = null
)

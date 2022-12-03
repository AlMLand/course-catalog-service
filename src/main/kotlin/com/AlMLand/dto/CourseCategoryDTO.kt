package com.AlMLand.dto

import com.AlMLand.dto.enums.Category
import java.util.*

data class CourseCategoryDTO(
    var category: Category,
    val id: UUID?,
    var description: String? = null
)

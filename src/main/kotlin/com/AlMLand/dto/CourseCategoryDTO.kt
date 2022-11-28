package com.AlMLand.dto

import com.AlMLand.dto.enums.Category

data class CourseCategoryDTO(
    var category: Category,
    val id: Int?,
    var description: String? = null
)

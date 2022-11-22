package com.AlMLand.dto

import javax.validation.constraints.NotBlank

data class CourseDTO(
    @field:NotBlank(message = "CourseDTO.name must not be blank")
    val name: String,
    @get:NotBlank(message = "CourseDTO.category must not be blank")
    val category: String,
    val id: Int?
)

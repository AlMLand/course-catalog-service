package com.AlMLand.dto

import javax.validation.constraints.NotBlank

data class InstructorDTO(
    @field:NotBlank(message = "Instructor.name must not be blank")
    var name: String,
    val id: Int?
)

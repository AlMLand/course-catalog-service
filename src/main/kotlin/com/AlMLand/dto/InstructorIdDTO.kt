package com.AlMLand.dto

import javax.validation.constraints.NotBlank

data class InstructorIdDTO(
    @field:NotBlank(message = "Instructor.firstname must not be blank or not be null")
    val firstName: String,
    @field:NotBlank(message = "Instructor.lastname must not be blank or not be null")
    val lastName: String
)

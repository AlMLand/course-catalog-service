package com.AlMLand.dto

import javax.validation.constraints.NotNull

data class InstructorDTO(
    @field:NotNull(message = "InstructorDTO.instructorId must not be null")
    val instructorId: InstructorIdDTO,
    var created: Boolean = false
)

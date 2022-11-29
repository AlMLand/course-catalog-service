package com.AlMLand.dto

import com.fasterxml.jackson.annotation.JsonFormat
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CourseDTO(
    @field:NotBlank(message = "CourseDTO.name must not be blank")
    val name: String,

    @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY])
    @get:NotNull(message = "CourseDTO.category must not be null")
    @field:NotEmpty(message = "CourseDTO.category must not be empty")
    val category: MutableList<CourseCategoryDTO>,

    val id: Int?,

    @field:NotNull(message = "CourseDTO.instructorIdDTO must not be null")
    var instructorId: InstructorIdDTO
)

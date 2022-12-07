package com.AlMLand.examples

import com.AlMLand.dto.InstructorDTO

interface InstructorServiceInt {
    fun createInstructor(dto: InstructorDTO): InstructorDTO
}
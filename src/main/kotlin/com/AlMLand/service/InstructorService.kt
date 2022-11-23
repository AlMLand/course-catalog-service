package com.AlMLand.service

import com.AlMLand.dto.InstructorDTO
import com.AlMLand.entity.Instructor
import com.AlMLand.repository.InstructorRepository
import org.springframework.stereotype.Service

@Service
class InstructorService(private val instructorRepository: InstructorRepository) {

    fun createInstructor(instructorDTO: InstructorDTO): InstructorDTO {
        if (instructorRepository.existsByName(instructorDTO.name)) return instructorDTO
        return instructorRepository.save(instructorDTO
            .let { Instructor(it.name, it.id) }).let { InstructorDTO(it.name, it.id) }
    }

}

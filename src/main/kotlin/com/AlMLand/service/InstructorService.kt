package com.AlMLand.service

import com.AlMLand.dto.InstructorDTO
import com.AlMLand.dto.InstructorIdDTO
import com.AlMLand.entity.Instructor
import com.AlMLand.entity.InstructorId
import com.AlMLand.repository.InstructorRepository
import org.springframework.stereotype.Service

@Service
class InstructorService(private val repository: InstructorRepository) {

    fun createInstructor(dto: InstructorDTO): InstructorDTO {
        return if (repository.existsByInstructorId_FirstNameAndInstructorId_LastName(
                dto.instructorId.firstName,
                dto.instructorId.lastName
            )
        ) dto
        else
            InstructorDTO(repository.save(Instructor(dto.instructorId.let { iid ->
                InstructorId(
                    iid.firstName,
                    iid.lastName
                )
            })).instructorId.let { iidDTO ->
                InstructorIdDTO(
                    iidDTO.firstName,
                    iidDTO.lastName
                )
            }, true)
    }

}

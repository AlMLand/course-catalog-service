package com.AlMLand.service;

import com.AlMLand.dto.InstructorDTO;
import com.AlMLand.dto.InstructorIdDTO;
import com.AlMLand.entity.Instructor;
import com.AlMLand.entity.InstructorId;
import com.AlMLand.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstructorServiceJava {
    private InstructorRepository repository;

    public InstructorServiceJava(final InstructorRepository repository) {
        this.repository = repository;
    }

    public void setRepository(final InstructorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public InstructorDTO createInstructor(final InstructorDTO dto) {
        if (repository.existsByInstructorId_FirstNameAndInstructorId_LastName(dto.getInstructorId().getFirstName(),
                dto.getInstructorId().getLastName())) {
            return dto;
        } else {
            return toDTO(repository.save(toEntity(dto)));
        }
    }

    private InstructorDTO toDTO(final Instructor entity) {
        return new InstructorDTO(new InstructorIdDTO(entity.getInstructorId().getFirstName(), entity.getInstructorId().getLastName()), true);
    }

    private Instructor toEntity(final InstructorDTO dto) {
        return new Instructor(new InstructorId(dto.getInstructorId().getFirstName(), dto.getInstructorId().getLastName()));
    }

}

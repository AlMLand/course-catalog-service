package com.AlMLand.controller

import com.AlMLand.dto.InstructorDTO
import com.AlMLand.service.InstructorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/v1/instructors")
class InstructorController(private val instructorService: InstructorService) {

    @PostMapping
    fun createInstructor(@Valid @RequestBody instructorDTO: InstructorDTO): ResponseEntity<InstructorDTO> {
        val newInstructorDTO = instructorService.createInstructor(instructorDTO)
        newInstructorDTO.id ?: return ResponseEntity.status(HttpStatus.CONFLICT).body(instructorDTO)
        return ResponseEntity.created(URI("/v1/instructors/${newInstructorDTO.id}")).body(newInstructorDTO)
    }

}
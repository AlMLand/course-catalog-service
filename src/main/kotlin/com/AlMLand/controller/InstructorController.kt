package com.AlMLand.controller

import com.AlMLand.dto.InstructorDTO
import com.AlMLand.service.InstructorService
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import javax.validation.Valid

private const val PATH = "/v1/instructors"

@Validated
@RestController
@RequestMapping(PATH)
class InstructorController(private val service: InstructorService) {

    @PostMapping
    fun createInstructor(@Valid @RequestBody dto: InstructorDTO): ResponseEntity<InstructorDTO> {
        val newDTO = service.createInstructor(dto)
        return if (!newDTO.created) ResponseEntity.status(CONFLICT).body(newDTO)
        else ResponseEntity.created(URI(PATH)).body(newDTO)
    }

}
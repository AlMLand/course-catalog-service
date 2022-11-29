package com.AlMLand.controller

import com.AlMLand.dto.InstructorDTO
import com.AlMLand.dto.InstructorIdDTO
import com.AlMLand.service.InstructorService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.util.UriComponentsBuilder

@ActiveProfiles("test")
@WebMvcTest(controllers = [InstructorController::class])
class InstructorControllerUnitTest2 @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var service: InstructorService

    @Test
    fun `createInstructor - when instructor with this firstname exists, than status 409 and the same dto as body`() {
        val instructorDTO = InstructorDTO(InstructorIdDTO("firstname1", "lastname1"))
        val instructorDTOAsJson = objectMapper.writeValueAsString(instructorDTO)
        `when`(service.createInstructor(instructorDTO)).thenReturn(instructorDTO)

        val uri = UriComponentsBuilder.fromUriString("/v1/instructors").toUriString()
        val response = mockMvc.perform(
            post(uri)
                .content(instructorDTOAsJson)
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isConflict)
            .andReturn().response.contentAsString

        Assertions.assertThat(response).isEqualTo(instructorDTOAsJson)
    }

    @Test
    fun `createInstructor - when create instructor is successful, than status 201, the new dto with id as body`() {
        val instructorDTO = InstructorDTO(InstructorIdDTO("firstname1", "lastname1"))
        val expectedInstructorDTO = InstructorDTO(InstructorIdDTO("firstname1", "lastname1"), true)

        `when`(service.createInstructor(instructorDTO)).thenReturn(expectedInstructorDTO)

        val uri = UriComponentsBuilder.fromUriString("/v1/instructors").toUriString()
        val response = mockMvc.perform(
            post(uri)
                .content(objectMapper.writeValueAsString(instructorDTO))
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated)
            .andReturn().response.contentAsString

        Assertions.assertThat(response).isEqualTo(objectMapper.writeValueAsString(expectedInstructorDTO))
    }
}
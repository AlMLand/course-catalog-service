package com.AlMLand.controller

import com.AlMLand.dto.InstructorDTO
import com.AlMLand.service.InstructorService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@WebMvcTest(controllers = [InstructorController::class])
class InstructorControllerUnitTest(@Autowired private val webTestClient: WebTestClient) {

    @MockkBean
    private lateinit var service: InstructorService

    @Test
    fun `createInstructor - when instructor with this firstname exists, than status 409 and the same dto as body`() {
        val instructorDTO = InstructorDTO("testName", null)
        every { service.createInstructor(instructorDTO) } returns instructorDTO

        val uri = UriComponentsBuilder.fromUriString("/v1/instructors").toUriString()
        val response = webTestClient.post()
            .uri(uri)
            .bodyValue(instructorDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(CONFLICT)
            .expectBody(InstructorDTO::class.java)
            .returnResult().responseBody
        Assertions.assertThat(response).isEqualTo(instructorDTO)
    }

    @Test
    fun `createInstructor - when create instructor is successful, than status 201, header Location, the new dto with id as body`() {
        val instructorDTO = InstructorDTO("testName", null)
        val createdInstructorDTO = InstructorDTO("testName", 1)
        every { service.createInstructor(instructorDTO) } returns createdInstructorDTO

        val uri = UriComponentsBuilder.fromUriString("/v1/instructors").toUriString()
        val response = webTestClient.post()
            .uri(uri)
            .bodyValue(instructorDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location("/v1/instructors/1")
            .expectBody(InstructorDTO::class.java)
            .returnResult().responseBody
        Assertions.assertThat(response).isEqualTo(createdInstructorDTO)
    }

}
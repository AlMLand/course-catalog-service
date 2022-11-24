package com.AlMLand.controller

import com.AlMLand.dto.InstructorDTO
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InstructorControllerIntegrationTest(@Autowired private val webTestClient: WebTestClient) {

    @SqlGroup(
        Sql(scripts = ["/db/test-data-course-create.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    )
    @Test
    fun `createInstructor - when instructor with this name exists, than status 409 and the same dto as body`() {
        val instructorDTO = InstructorDTO("testInstructor1", null)
        val response = webTestClient.post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody(InstructorDTO::class.java)
            .returnResult().responseBody
        Assertions.assertThat(response).isEqualTo(instructorDTO)
    }

    @Test
    fun `createInstructor - when create instructor is successful, than status 201, header Location, the new dto with id as body`() {
        val instructorDTO = InstructorDTO("testInstructor", null)
        val expectedInstructorDTO = InstructorDTO("testInstructor", 1)
        val response = webTestClient.post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(InstructorDTO::class.java)
            .returnResult().responseBody
        Assertions.assertThat(response).isEqualTo(expectedInstructorDTO)
    }

}

package com.AlMLand.controller

import com.AlMLand.dto.InstructorDTO
import com.AlMLand.dto.InstructorIdDTO
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
class InstructorControllerIntegrationTest(@Autowired private val webTestClient: WebTestClient) {

    private companion object TestContainer {
        @Container
        val postgresDB = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:15.1-alpine")).apply {
            withDatabaseName("testdb-for-kotlin-course")
            withUsername("alex")
            withPassword("secret")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresDB::getJdbcUrl)
            registry.add("spring.datasource.username", postgresDB::getUsername)
            registry.add("spring.datasource.password", postgresDB::getPassword)
        }
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data-course-create.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `createInstructor - when instructor with this firstname exists, than status 409 and the same dto as body`() {
        val instructorDTO = InstructorDTO(InstructorIdDTO("firstName", "lastName"))
        val response = webTestClient.post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus().isEqualTo(CONFLICT)
            .expectBody(InstructorDTO::class.java)
            .returnResult().responseBody
        Assertions.assertThat(response).isEqualTo(instructorDTO)
    }

    @Test
    fun `createInstructor - when create instructor is successful, than status 201, the new dto with id as body`() {
        val instructorDTO = InstructorDTO(InstructorIdDTO("firstName1", "lastName1"))
        val expectedInstructorDTO = InstructorDTO(InstructorIdDTO("firstName1", "lastName1"), true)
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

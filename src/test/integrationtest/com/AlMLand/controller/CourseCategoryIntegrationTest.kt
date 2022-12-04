package com.AlMLand.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.*

@Testcontainers
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CourseCategoryIntegrationTest(@Autowired private val webTestClient: WebTestClient) {
    private companion object {
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
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `getIdByCategoryAndDescription - when success search, than status 200 and expected uuid`() {
        val expectedUuid = "00001234-0056-0078-0090-000000123456"
        val response = webTestClient.get()
            .uri(
                UriComponentsBuilder.fromUriString("/v1/categories/uuid")
                    .queryParam("category", "DEVELOPMENT")
                    .queryParam("description", "description1")
                    .toUriString()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(UUID::class.java)
            .returnResult().responseBody
        assertThat(response.toString()).isEqualTo(expectedUuid)
    }

}

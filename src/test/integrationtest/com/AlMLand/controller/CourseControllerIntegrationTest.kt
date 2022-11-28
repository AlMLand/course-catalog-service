package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.CourseDTO
import com.AlMLand.dto.enums.Category
import com.AlMLand.dto.enums.Category.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
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
import java.net.URI
import java.util.stream.Stream

@Testcontainers
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class CourseControllerIntegrationTest(@Autowired private val webTestClient: WebTestClient) {
    private companion object TestUtil {
        @JvmStatic
        fun getAllCoursesWithParamsNameAndCategory(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "tnam", DEVELOPMENT, listOf(
                    CourseDTO("testName1", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testDescription1")), 1, 1),
                    CourseDTO(
                        "testName2", mutableListOf(
                            CourseCategoryDTO(DEVELOPMENT, 2, "testDescription2"),
                            CourseCategoryDTO(MANAGEMENT, 3, "testDescription3")
                        ), 2, 2
                    )
                )
            ),
            Arguments.arguments(
                "name", MANAGEMENT, listOf(
                    CourseDTO(
                        "testName2", mutableListOf(
                            CourseCategoryDTO(DEVELOPMENT, 2, "testDescription2"),
                            CourseCategoryDTO(MANAGEMENT, 3, "testDescription3")
                        ), 2, 2
                    )
                )
            )
        )

        @JvmStatic
        fun getAllCoursesWithParamCategory(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                DEVELOPMENT, listOf(
                    CourseDTO("testName1", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testDescription1")), 1, 1),
                    CourseDTO(
                        "testName2", mutableListOf(
                            CourseCategoryDTO(DEVELOPMENT, 2, "testDescription2"),
                            CourseCategoryDTO(MANAGEMENT, 3, "testDescription3")
                        ), 2, 2
                    )
                )
            ),
            Arguments.arguments(
                MANAGEMENT, listOf(
                    CourseDTO(
                        "testName2", mutableListOf(
                            CourseCategoryDTO(DEVELOPMENT, 2, "testDescription2"),
                            CourseCategoryDTO(MANAGEMENT, 3, "testDescription3")
                        ), 2, 2
                    )
                )
            )
        )

        @JvmStatic
        fun getAllCoursesWithParamName(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "TnA", listOf(
                    CourseDTO("testName1", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testDescription1")), 1, 1),
                    CourseDTO(
                        "testName2", mutableListOf(
                            CourseCategoryDTO(DEVELOPMENT, 2, "testDescription2"),
                            CourseCategoryDTO(MANAGEMENT, 3, "testDescription3")
                        ), 2, 2
                    )
                )
            ),
            Arguments.arguments(
                "tname1", listOf(
                    CourseDTO("testName1", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testDescription1")), 1, 1)
                )
            )
        )

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

    @Test
    fun `getAllCourses - when no courses are available, than return list with size 0`() {
        val response = webTestClient.get()
            .uri("/v1/courses")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(listOf<CourseDTO>())
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `getAllCourses - return list with size 2`() {
        val expectedList = listOf(
            CourseDTO("testName1", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testDescription1")), 1, 1),
            CourseDTO(
                "testName2", mutableListOf(
                    CourseCategoryDTO(DEVELOPMENT, 2, "testDescription2"),
                    CourseCategoryDTO(MANAGEMENT, 3, "testDescription3")
                ), 2, 2
            )
        )

        val response = webTestClient.get()
            .uri("/v1/courses")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(expectedList)
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @ParameterizedTest
    @MethodSource("getAllCoursesWithParamCategory")
    fun `getAllCourses with param category - first approach list with size 2, second approach list with size 1`(
        category: Category,
        expectedList: List<CourseDTO>
    ) {
        val uri = UriComponentsBuilder.fromUriString("/v1/courses")
            .queryParam("category", category).toUriString()

        val response = webTestClient.get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(expectedList)
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @ParameterizedTest
    @MethodSource("getAllCoursesWithParamsNameAndCategory")
    fun `getAllCourses with param name and category - first approach list with size 2, second approach list with size 1`(
        name: String,
        category: Category,
        expectedList: List<CourseDTO>
    ) {
        val uri = UriComponentsBuilder.fromUriString("/v1/courses")
            .queryParam("name", name).queryParam("category", category).toUriString()

        val response = webTestClient.get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(expectedList)
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @ParameterizedTest
    @MethodSource("getAllCoursesWithParamName")
    fun `getAllCourses with param name - first approach size is 2, second approach size is 1`(
        name: String,
        courses: List<CourseDTO>
    ) {
        val uri = UriComponentsBuilder.fromUri(URI("/v1/courses"))
            .queryParam("name", name).toUriString()

        val response = webTestClient.get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(courses)
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `getCourse - get course with id - 1`() {
        val courseId = 1
        val expectedCourseDTO =
            CourseDTO("testName1", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testDescription1")), courseId, 1)

        val response = webTestClient.get()
            .uri("/v1/courses/{id}", courseId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(expectedCourseDTO)
    }

    @Test
    fun `getCourse - get course with id, what is not available in db - status not found`() {
        val courseId = 1
        val response = webTestClient.get()
            .uri("/v1/courses/{id}", courseId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult().responseBody

        Assertions.assertTrue(response == null)
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `updateCourse - should have status 404, body with the same data, id = null`() {
        val courseId = 10
        val courseDTO =
            CourseDTO("testName1", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), null, 1)

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .contentType(APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isNotFound
            .expectBody(courseDTO::class.java)
            .returnResult().responseBody

        assertThat(courseDTO).isEqualTo(response)
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `updateCourse - should have status 200, body with the another data, id is the same, header location`() {
        val courseId = 1
        val courseCategoryDTOS = mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testDescription1"))
        val courseDTO = CourseDTO("testName1Changed", courseCategoryDTOS, null, 1)
        val expectedCourseDTO = CourseDTO("testName1Changed", courseCategoryDTOS, courseId, 1)
        val expectedLocationHeader = "v1/courses/1"

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .contentType(APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isOk
            .expectHeader().location(expectedLocationHeader)
            .expectBody(courseDTO::class.java)
            .returnResult().responseBody

        assertThat(expectedCourseDTO).isEqualTo(response)
    }

    @Test
    fun `updateCourse - when name is blank, than status 400, body with the same data`() {
        val courseId = 1
        val courseDTO = CourseDTO("", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "category")), null, 1)

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .contentType(APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo("CourseDTO.name must not be blank")
    }

    @Test
    fun `updateCourse - when category is empty, than status 400, in body is error message`() {
        val courseId = 1
        val courseDTO = CourseDTO("name", mutableListOf(), null, 1)

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .contentType(APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo("CourseDTO.category must not be empty")
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `delete - should have status 200`() {
        val courseId = 1

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isOk
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `delete - should have status 404`() {
        val courseId = 10

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isNotFound
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `createCourse - should have status 409(conflict), body with the same data, id = null`() {
        val courseDTO =
            CourseDTO("testName1", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), null, 1)
        val response = webTestClient.post()
            .uri("/v1/courses")
            .contentType(APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isEqualTo(CONFLICT)
            .expectBody(courseDTO::class.java)
            .returnResult().responseBody

        assertThat(courseDTO).isEqualTo(response)
    }

    @SqlGroup(
        Sql(scripts = ["/db/test-data-course-create.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `createCourse - should have status 201, header location, body with the same data, id != null`() {
        val courseDTO =
            CourseDTO("testName", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testDescription")), null, 1)
        val expectedId = 1
        val expectedLocationHeader = "v1/courses/1"

        val response = webTestClient.post()
            .uri("v1/courses")
            .bodyValue(courseDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location(expectedLocationHeader)
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response?.id).isEqualTo(expectedId)
        assertThat(response?.name).isEqualTo(courseDTO.name)
        assertThat(response?.category).isEqualTo(courseDTO.category)
    }

    @Test
    fun `createCourse - create new course with name is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("", mutableListOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)

        val response = webTestClient.post()
            .uri("v1/courses")
            .bodyValue(courseDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(courseDTO)
    }

    @Test
    fun `createCourse - create new course with category is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("testName", mutableListOf(), null, 1)

        val response = webTestClient.post()
            .uri("v1/courses")
            .bodyValue(courseDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(courseDTO)
    }
}
package com.AlMLand.controller

import com.AlMLand.dto.CourseDTO
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.stream.Stream

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseControllerIntegrationTest(@Autowired private val webTestClient: WebTestClient) {

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @ParameterizedTest
    @MethodSource("getArguments")
    fun `getCourseByNameLike - when courses founded than status 200`(name: String, expectedSize: Int) {
        val expectedCourses = listOf(
            CourseDTO("testName1", "testCategory1", 1),
            CourseDTO("testName2", "testCategory2", 2)
        )
        val response = webTestClient.get()
            .uri("/v1/courses/names/{name}", name)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response!!.size).isEqualTo(expectedSize)
    }

    companion object TestUtil {
        @JvmStatic
        fun getArguments(): Stream<Arguments> = Stream.of(
            Arguments.arguments("NaMe", 2),
            Arguments.arguments("ame2", 1)
        )
    }

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun `getCourseByNameLike - when no courses founded than status 204`() {
        val name = "example"
        val response = webTestClient.get()
            .uri("/v1/courses/names/{name}", name)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response!!.size).isEqualTo(0)
    }

    @Test
    fun `getAllCourses - when no courses are available, than return list with size 0`() {
        val response = webTestClient.get()
            .uri("/v1/courses")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(listOf<CourseDTO>())
    }

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun `getAllCourses - return list with size 2`() {
        val expectedList = listOf(
            CourseDTO("testName1", "testCategory1", 1),
            CourseDTO("testName2", "testCategory2", 2)
        )

        val response = webTestClient.get()
            .uri("/v1/courses")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(expectedList).isEqualTo(response)
    }

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun `getCourse - get course with id - 1`() {
        val courseId = 1
        val expectedCourseDTO = CourseDTO("testName1", "testCategory1", courseId)

        val response = webTestClient.get()
            .uri("/v1/courses/{id}", courseId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(expectedCourseDTO).isEqualTo(response)
    }

    @Test
    fun `getCourse - get course with id, what is not available in db - status not found`() {
        val courseId = 1
        val response = webTestClient.get()
            .uri("/v1/courses/{id}", courseId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult().responseBody

        Assertions.assertTrue(response == null)
    }

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun `updateCourse - should have status 404, body with the same data, id = null`() {
        val courseId = 10
        val courseDTO = CourseDTO("testName1", "testCategory1", null)

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isNotFound
            .expectBody(courseDTO::class.java)
            .returnResult().responseBody

        assertThat(courseDTO).isEqualTo(response)
    }

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun `updateCourse - should have status 200, body with the another data, id is the same, header location`() {
        val courseId = 1
        val courseDTO = CourseDTO("testName1Changed", "testCategory1Changed", null)
        val expectedCourseDTO = CourseDTO("testName1Changed", "testCategory1Changed", courseId)
        val expectedLocationHeader = "v1/courses/1"

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .contentType(MediaType.APPLICATION_JSON)
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
        val courseDTO = CourseDTO("", "category", null)

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo("CourseDTO.name must not be blank")
    }

    @Test
    fun `updateCourse - when category is blank, than status 400, body with the same data`() {
        val courseId = 1
        val courseDTO = CourseDTO("name", "", null)

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo("CourseDTO.category must not be blank")
    }

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun `delete - should have status 200`() {
        val courseId = 1

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isOk
    }

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun `delete - should have status 404`() {
        val courseId = 10

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isNotFound
    }

    @Sql(
        scripts = ["/db/test-data.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    fun `createCourse - should have status 409(conflict), body with the same data, id = null`() {
        val courseDTO = CourseDTO("testName1", "testCategory1", null)
        val response = webTestClient.post()
            .uri("/v1/courses")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody(courseDTO::class.java)
            .returnResult().responseBody

        assertThat(courseDTO).isEqualTo(response)
    }

    @Test
    fun `createCourse - should have status 201, header location, body with the same data, id != null`() {
        val courseDTO = CourseDTO("testName", "testCategory", null)
        val expectedId = 1
        val expectedLocationHeader = "v1/courses/1"

        val result = webTestClient.post()
            .uri("v1/courses")
            .bodyValue(courseDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location(expectedLocationHeader)
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(result?.id).isEqualTo(expectedId)
        assertThat(result?.name).isEqualTo(courseDTO.name)
        assertThat(result?.category).isEqualTo(courseDTO.category)
    }

    @Test
    fun `createCourse - create new course with name is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("", "testCategory", null)

        val response = webTestClient.post()
            .uri("v1/courses")
            .bodyValue(courseDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(courseDTO)
    }

    @Test
    fun `createCourse - create new course with category is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("testName", "", null)

        val response = webTestClient.post()
            .uri("v1/courses")
            .bodyValue(courseDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(courseDTO)
    }
}
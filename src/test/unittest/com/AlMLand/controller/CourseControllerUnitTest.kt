package com.AlMLand.controller

import com.AlMLand.dto.CourseDTO
import com.AlMLand.service.CourseService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.stream.Stream

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@WebMvcTest(controllers = [CourseController::class])
class CourseControllerUnitTest(@Autowired private val webTestClient: WebTestClient) {

    @MockkBean
    private lateinit var courseService: CourseService

    @ParameterizedTest
    @ValueSource(strings = ["test", "Category"])
    fun `getCourseByCategoryLike - when courses founded than status 200`(category: String) {
        val expectedCourses = listOf(
            CourseDTO("testName1", "testCategory1", 1),
            CourseDTO("testName2", "testCategory2", 2)
        )
        every { courseService.findCourseByNameLike(category) } returns expectedCourses

        val response = webTestClient.get()
            .uri("/v1/courses/categories/{category}", category)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(expectedCourses)
    }

    @Test
    fun `getCourseByCategoryLike - when no courses founded than status 204`() {
        val category = "example"
        val expectedCourses = listOf<CourseDTO>()
        every { courseService.findCourseByNameLike(category) } returns expectedCourses

        val response = webTestClient.get()
            .uri("/v1/courses/categories/{name}", category)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `handleAllExceptions - check the controller advice`() {
        val courseId = 1
        every { courseService.deleteCourse(courseId) } throws IllegalArgumentException()

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `delete - when successful, than return status 200`() {
        val courseId = 1
        every { courseService.deleteCourse(courseId) } returns true

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `delete - when course not found, than status 404`() {
        val courseId = 1
        every { courseService.deleteCourse(courseId) } returns false

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `updateCourse - update is successful - status 200, header has location to this course, updated course in body `() {
        val courseId = 1
        val courseDTO = CourseDTO("updatedName", "updatedCategory", null)
        val updatedCourseDTO = CourseDTO("updatedName", "updatedCategory", 1)
        val expectedLocationHeader = "v1/courses/1"

        every { courseService.updateCourses(courseId, courseDTO) } returns updatedCourseDTO

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isOk
            .expectHeader().location(expectedLocationHeader)
            .expectBody(courseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(updatedCourseDTO)
    }

    @Test
    fun `updateCourse - course by id is not found - status 404`() {
        val courseId = 1
        val courseDTO = CourseDTO("name", "category", null)

        every { courseService.updateCourses(courseId, courseDTO) } returns courseDTO

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isNotFound
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(courseDTO).isEqualTo(response)
    }


    @Test
    fun `updateCourse - when name is blank, than status 400, body with the same data`() {
        val courseId = 1
        val courseDTO = CourseDTO("", "category", null)

        every { courseService.updateCourses(courseId, courseDTO) } returns courseDTO

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
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

        every { courseService.updateCourses(courseId, courseDTO) } returns courseDTO

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo("CourseDTO.category must not be blank")
    }


    @Test
    fun `getAllCourses without param name - when no courses are available, than return list with size 0`() {
        every { courseService.findAllCourses(null) } returns listOf()

        val response = webTestClient.get()
            .uri("/v1/courses")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(0).isEqualTo(response?.size)
    }

    @Test
    fun `getAllCourses without param name - return list with size 2`() {
        val expectedList = listOf(
            CourseDTO("name1", "category1", 1),
            CourseDTO("name2", "category2", 2)
        )

        every { courseService.findAllCourses(null) } returns expectedList

        val actualList = webTestClient.get()
            .uri("/v1/courses")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(expectedList).isEqualTo(actualList)
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    fun `getAllCourses with param name - first approach size is 2, second approach size is 1`(
        name: String,
        expectedSize: Int,
        courses: List<CourseDTO>
    ) {

        every { courseService.findAllCourses(name) } returns courses

        val response = webTestClient.get()
            .uri("/v1/courses?name=$name")
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
            Arguments.arguments(
                "am", 2, listOf(
                    CourseDTO("name1", "category1", 1),
                    CourseDTO("name2", "category2", 2)
                )
            ),
            Arguments.arguments(
                "e2", 1, listOf(
                    CourseDTO("name2", "category2", 2)
                )
            )
        )
    }

    @Test
    fun `getCourse - get course with id - 1`() {
        val expectedCourseDTO = CourseDTO("testName", "testCategory", 1)
        val courseId = 1

        every { courseService.findCourse(courseId) } returns expectedCourseDTO

        val actualCourseDTO = webTestClient.get()
            .uri("/v1/courses/{id}", courseId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(expectedCourseDTO).isEqualTo(actualCourseDTO)
    }

    @Test
    fun `getCourse - get course with id, what is not available in db - status not found`() {
        val expectedCourseDTO = CourseDTO("testName", "defaultCategory", 1)
        val courseId = 1

        every { courseService.findCourse(courseId) } returns null

        val responseBody = webTestClient.get()
            .uri("/v1/courses/{id}", courseId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult().responseBody

        org.junit.jupiter.api.Assertions.assertTrue(responseBody!!.isEmpty())
    }

    @Test
    fun `createCourse - create new course, should give back the courseDTO with the same data, status 409, id = null`() {
        val courseDTO = CourseDTO("testName", "testCategory", null)
        val expectedLocationHeader = "v1/courses/1"

        every { courseService.createCourse(any()) } returns courseDTO

        val response = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(courseDTO).isEqualTo(response)
    }

    @Test
    fun `createCourse - create new course, should give back the courseDTO with the same data, status 201, id = 1`() {
        val courseDTO = CourseDTO("testName", "testCategory", null)
        val expectedCourseDTO = CourseDTO("testName", "testCategory", 1)
        val expectedLocationHeader = "v1/courses/1"

        every { courseService.createCourse(any()) } returns expectedCourseDTO

        val result = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location(expectedLocationHeader)
            .expectBody(CourseDTO::class.java)
            .returnResult()

        assertThat(expectedCourseDTO).isEqualTo(result.responseBody)
    }

    @Test
    fun `createCourse - create new course with name = "", should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("", "testCategory", null)

        val result = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(CourseDTO::class.java)
            .returnResult()

        assertThat(courseDTO).isEqualTo(result.responseBody)
    }

    @Test
    fun `createCourse - create new course with category = "", should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("testName", "", null)

        val result = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(CourseDTO::class.java)
            .returnResult()

        assertThat(courseDTO).isEqualTo(result.responseBody)
    }
}

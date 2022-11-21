package com.AlMLand.controller

import com.AlMLand.dto.CourseDTO
import com.AlMLand.service.CourseService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
@WebMvcTest(controllers = [CourseController::class])
class CourseControllerUnitTest(@Autowired private val webTestClient: WebTestClient) {

    @MockkBean
    private lateinit var courseService: CourseService

    @Test
    fun `getAllCourses - when no courses are available, than return list with size 0`() {
        every { courseService.findAllCourses() } returns listOf()

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
    fun `getAllCourses - return list with size 2`() {
        val expectedList = listOf(
            CourseDTO("name1", "category1", 1),
            CourseDTO("name2", "category2", 2)
        )

        every { courseService.findAllCourses() } returns expectedList

        val actualList = webTestClient.get()
            .uri("/v1/courses")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(expectedList).isEqualTo(actualList)
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
    fun `addCourse - create new course, should give back the courseDTO with the same data, status 409, id = null`() {
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
    fun `addCourse - create new course, should give back the courseDTO with the same data, status 201, id = 1`() {
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
}

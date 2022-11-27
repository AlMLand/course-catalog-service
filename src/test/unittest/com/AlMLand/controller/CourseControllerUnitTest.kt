package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.CourseDTO
import com.AlMLand.dto.enums.Category
import com.AlMLand.dto.enums.Category.DEVELOPMENT
import com.AlMLand.dto.enums.Category.QA
import com.AlMLand.service.CourseService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
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

    companion object TestUtil {
        @JvmStatic
        fun getAllCoursesWithParamsNameAndCategory(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "ame", DEVELOPMENT, 2, listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "category1")), 1, 1),
                    CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "category2")), 2, 1)
                )
            ),
            Arguments.arguments(
                "na", QA, 1, listOf(
                    CourseDTO("name4", listOf(CourseCategoryDTO(QA, 3, "category3")), 4, 5)
                )
            )
        )

        @JvmStatic
        fun getAllCoursesWithParamCategory(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                DEVELOPMENT, 2, listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "category1")), 1, 1),
                    CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "category2")), 2, 1)
                )
            ),
            Arguments.arguments(
                QA, 1, listOf(
                    CourseDTO("name4", listOf(CourseCategoryDTO(QA, 3, "category3")), 4, 5)
                )
            )
        )

        @JvmStatic
        fun getAllCoursesWithParamName(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "am", 2, listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "category1")), 1, 1),
                    CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "category2")), 2, 1)
                )
            ),
            Arguments.arguments(
                "e2", 1, listOf(
                    CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "category")), 2, 1)
                )
            )
        )
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
        val courseDTO =
            CourseDTO("updatedName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "updatedCategory")), null, 1)
        val updatedCourseDTO =
            CourseDTO("updatedName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "updatedCategory")), 1, 1)
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
        val courseDTO = CourseDTO("name", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)

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
        val courseDTO = CourseDTO("", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)

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
        val courseDTO = CourseDTO("name", listOf(), null, 1)

        every { courseService.updateCourses(courseId, courseDTO) } returns courseDTO

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo("CourseDTO.category must not be empty")
    }


    @Test
    fun `getAllCourses without params name, category - when no courses are available, than return list with size 0`() {
        every { courseService.findAllCourses(null, null) } returns listOf()

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
    fun `getAllCourses without params name, category - return list with size 2`() {
        val expectedList = listOf(
            CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), 1, 1),
            CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "testCategory2")), 2, 1)
        )

        every { courseService.findAllCourses(null, null) } returns expectedList

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
    @MethodSource("getAllCoursesWithParamName")
    fun `getAllCourses with param name - first approach size is 2, second approach size is 1`(
        name: String,
        expectedSize: Int,
        courses: List<CourseDTO>
    ) {

        every { courseService.findAllCourses(name, null) } returns courses

        val response = webTestClient.get()
            .uri("/v1/courses?name=$name")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response!!.size).isEqualTo(expectedSize)
    }

    @ParameterizedTest
    @MethodSource("getAllCoursesWithParamCategory")
    fun `getAllCourses with param category - first approach size is 2, second approach size is 1`(
        category: Category,
        expectedSize: Int,
        courses: List<CourseDTO>
    ) {

        every { courseService.findAllCourses(null, category) } returns courses

        val response = webTestClient.get()
            .uri("/v1/courses?category=$category")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response!!.size).isEqualTo(expectedSize)
    }

    @ParameterizedTest
    @MethodSource("getAllCoursesWithParamsNameAndCategory")
    fun `getAllCourses with params name, category - first approach size is 2, second approach size is 1`(
        name: String,
        category: Category,
        expectedSize: Int,
        courses: List<CourseDTO>
    ) {

        every { courseService.findAllCourses(name, category) } returns courses

        val response = webTestClient.get()
            .uri("/v1/courses?name=$name&category=$category")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response!!.size).isEqualTo(expectedSize)
    }

    @Test
    fun `getCourse - get course with id - 1`() {
        val expectedCourseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), 1, 1)
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
        val expectedCourseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "defaultCategory")), 1, 1)
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
        val courseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)
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
        val courseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)
        val expectedCourseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), 1, 1)
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
    fun `createCourse - create new course with name is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)

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
    fun `createCourse - create new course with category is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("testName", listOf(), null, 1)

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

package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.CourseDTO
import com.AlMLand.dto.InstructorIdDTO
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
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*
import java.util.UUID.*
import java.util.stream.Stream

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@WebMvcTest(controllers = [CourseController::class])
class CourseControllerUnitTest(@Autowired private val webTestClient: WebTestClient) {

    @MockkBean
    private lateinit var service: CourseService

    companion object TestUtil {
        @JvmStatic
        fun getAllCoursesWithParamsNameAndCategory(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "ame", DEVELOPMENT, 2, listOf(
                    CourseDTO(
                        "name1",
                        mutableListOf(
                            CourseCategoryDTO(
                                DEVELOPMENT,
                                fromString("1234-56-78-90-123456"),
                                "category1"
                            )
                        ),
                        fromString("1111-11-11-11-111111"),
                        InstructorIdDTO("firstname1", "lastname1")
                    ),
                    CourseDTO(
                        "name2",
                        mutableListOf(
                            CourseCategoryDTO(
                                DEVELOPMENT,
                                fromString("0987-65-43-21-098765"),
                                "category2"
                            )
                        ),
                        fromString("2222-22-22-22-222222"),
                        InstructorIdDTO("firstname2", "lastname2")
                    )
                )
            ),
            Arguments.arguments(
                "na", QA, 1, listOf(
                    CourseDTO(
                        "name4",
                        mutableListOf(CourseCategoryDTO(QA, fromString("4444-22-22-22-666666"), "category3")),
                        fromString("4444-44-44-44-444444"),
                        InstructorIdDTO("firstname5", "lastname5")
                    )
                )
            )
        )

        @JvmStatic
        fun getAllCoursesWithParamCategory(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                DEVELOPMENT, 2, listOf(
                    CourseDTO(
                        "name1",
                        mutableListOf(
                            CourseCategoryDTO(
                                DEVELOPMENT,
                                fromString("1234-56-78-90-123456"),
                                "category1"
                            )
                        ),
                        fromString("1111-11-11-11-111111"),
                        InstructorIdDTO("firstname1", "lastname1")
                    ),
                    CourseDTO(
                        "name2",
                        mutableListOf(
                            CourseCategoryDTO(
                                DEVELOPMENT,
                                fromString("0987-65-43-21-098765"),
                                "category2"
                            )
                        ),
                        fromString("2222-22-22-22-222222"),
                        InstructorIdDTO("firstname2", "lastname2")
                    )
                )
            ),
            Arguments.arguments(
                QA, 1, listOf(
                    CourseDTO(
                        "name4",
                        mutableListOf(CourseCategoryDTO(QA, fromString("4444-22-22-22-666666"), "category3")),
                        fromString("4444-44-44-44-444444"),
                        InstructorIdDTO("firstname5", "lastname5")
                    )
                )
            )
        )

        @JvmStatic
        fun getAllCoursesWithParamName(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "am", 2, listOf(
                    CourseDTO(
                        "name1",
                        mutableListOf(
                            CourseCategoryDTO(
                                DEVELOPMENT,
                                fromString("1234-56-78-90-123456"),
                                "category1"
                            )
                        ),
                        fromString("1111-11-11-11-111111"),
                        InstructorIdDTO("firstname1", "lastname1")
                    ),
                    CourseDTO(
                        "name2",
                        mutableListOf(
                            CourseCategoryDTO(
                                DEVELOPMENT,
                                fromString("0987-65-43-21-098765"),
                                "category2"
                            )
                        ),
                        fromString("2222-22-22-22-222222"),
                        InstructorIdDTO("firstname2", "lastname2")
                    )
                )
            ),
            Arguments.arguments(
                "e2", 1, listOf(
                    CourseDTO(
                        "name2",
                        mutableListOf(
                            CourseCategoryDTO(
                                DEVELOPMENT,
                                fromString("0987-65-43-21-098765"),
                                "category"
                            )
                        ),
                        fromString("2222-22-22-22-222222"),
                        InstructorIdDTO("firstname1", "lastname1")
                    )
                )
            )
        )
    }

    @Test
    fun `handleAllExceptions - check the controller advice`() {
        val courseId = fromString("1111-11-11-11-111111")
        every { service.deleteCourse(courseId) } throws IllegalArgumentException()

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `delete - when successful, than return status 200`() {
        val courseId = fromString("1111-11-11-11-111111")
        every { service.deleteCourse(courseId) } returns true

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `delete - when course not found, than status 404`() {
        val courseId = fromString("1111-11-11-11-111111")
        every { service.deleteCourse(courseId) } returns false

        webTestClient.delete()
            .uri("/v1/courses/{id}", courseId)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `updateCourse - update is successful - status 200, header has location to this course, updated course in body `() {
        val courseId = fromString("1111-11-11-11-111111")
        val courseDTO =
            CourseDTO(
                "updatedName",
                mutableListOf(
                    CourseCategoryDTO(
                        DEVELOPMENT,
                        fromString("1234-56-78-90-123456"),
                        "updatedCategory"
                    )
                ),
                null,
                InstructorIdDTO("firstname1", "lastname1")
            )
        val updatedCourseDTO =
            CourseDTO(
                "updatedName",
                mutableListOf(
                    CourseCategoryDTO(
                        DEVELOPMENT,
                        fromString("1234-56-78-90-123456"),
                        "updatedCategory"
                    )
                ),
                fromString("1111-11-11-11-111111"),
                InstructorIdDTO("firstname1", "lastname1")
            )

        every { service.updateCourses(courseId, courseDTO) } returns updatedCourseDTO

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isOk
            .expectHeader().location("v1/courses/00001111-0011-0011-0011-000000111111")
            .expectBody(courseDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(updatedCourseDTO)
    }

    @Test
    fun `updateCourse - course by id is not found - status 404`() {
        val courseId = fromString("1111-11-11-11-111111")
        val courseDTO = CourseDTO(
            "name",
            mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory")),
            null,
            InstructorIdDTO("firstname1", "lastname1")
        )

        every { service.updateCourses(courseId, courseDTO) } returns courseDTO

        val response = webTestClient.put()
            .uri("/v1/courses/{id}", courseId)
            .accept(APPLICATION_JSON)
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isNotFound
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(courseDTO).isEqualTo(response)
    }


    @Test
    fun `updateCourse - when name is blank, than status 400, body with the same data`() {
        val courseId = fromString("1111-11-11-11-111111")
        val courseDTO = CourseDTO(
            "",
            mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory")),
            null,
            InstructorIdDTO("firstname1", "lastname1")
        )

        every { service.updateCourses(courseId, courseDTO) } returns courseDTO

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
        val courseId = fromString("1111-11-11-11-111111")
        val courseDTO = CourseDTO("name", mutableListOf(), null, InstructorIdDTO("firstname1", "lastname1"))

        every { service.updateCourses(courseId, courseDTO) } returns courseDTO

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
        every { service.findAllCourses(null, null) } returns listOf()

        val response = webTestClient.get()
            .uri("/v1/courses")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(0).isEqualTo(response?.size)
    }

    @Test
    fun `getAllCourses without params name, category - return list with size 2`() {
        val expectedList = listOf(
            CourseDTO(
                "name1",
                mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory1")),
                fromString("1111-11-11-11-111111"),
                InstructorIdDTO("firstname1", "lastname1")
            ),
            CourseDTO(
                "name2",
                mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("0987-65-43-21-098765"), "testCategory2")),
                fromString("2222-22-22-22-222222"),
                InstructorIdDTO("firstname2", "lastname2")
            )
        )

        every { service.findAllCourses(null, null) } returns expectedList

        val actualList = webTestClient.get()
            .uri("/v1/courses")
            .accept(APPLICATION_JSON)
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

        every { service.findAllCourses(name, null) } returns courses

        val response = webTestClient.get()
            .uri("/v1/courses?name=$name")
            .accept(APPLICATION_JSON)
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

        every { service.findAllCourses(null, category) } returns courses

        val response = webTestClient.get()
            .uri("/v1/courses?category=$category")
            .accept(APPLICATION_JSON)
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

        every { service.findAllCourses(name, category) } returns courses

        val response = webTestClient.get()
            .uri("/v1/courses?name=$name&category=$category")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(response!!.size).isEqualTo(expectedSize)
    }

    @Test
    fun `getCourse - get course with id - 1`() {
        val expectedCourseDTO =
            CourseDTO(
                "testName",
                mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory")),
                fromString("1111-11-11-11-111111"),
                InstructorIdDTO("firstname1", "lastname1")
            )
        val courseId = fromString("1111-11-11-11-111111")

        every { service.findCourse(courseId) } returns expectedCourseDTO

        val actualCourseDTO = webTestClient.get()
            .uri("/v1/courses/{id}", courseId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(expectedCourseDTO).isEqualTo(actualCourseDTO)
    }

    @Test
    fun `getCourse - get course with id, what is not available in db - status not found`() {
        val expectedCourseDTO =
            CourseDTO(
                "testName",
                mutableListOf(
                    CourseCategoryDTO(
                        DEVELOPMENT,
                        fromString("1234-56-78-90-123456"),
                        "defaultCategory"
                    )
                ),
                fromString("1111-11-11-11-111111"),
                InstructorIdDTO("firstname1", "lastname1")
            )
        val courseId = fromString("1111-11-11-11-111111")

        every { service.findCourse(courseId) } returns null

        val responseBody = webTestClient.get()
            .uri("/v1/courses/{id}", courseId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult().responseBody

        org.junit.jupiter.api.Assertions.assertTrue(responseBody!!.isEmpty())
    }

    @Test
    fun `createCourse - create new course, should give back the courseDTO with the same data, status 409, id = null`() {
        val courseDTO =
            CourseDTO(
                "testName",
                mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory")),
                null,
                InstructorIdDTO("firstname1", "lastname1")
            )
        val expectedLocationHeader = "v1/courses/1"

        every { service.createCourse(any()) } returns courseDTO

        val response = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(CONFLICT)
            .expectBody(CourseDTO::class.java)
            .returnResult().responseBody

        assertThat(courseDTO).isEqualTo(response)
    }

    @Test
    fun `createCourse - create new course, should give back the courseDTO with the same data, status 201, id = 1`() {
        val courseDTO =
            CourseDTO(
                "testName",
                mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory")),
                null,
                InstructorIdDTO("firstname1", "lastname1")
            )
        val expectedCourseDTO =
            CourseDTO(
                "testName",
                mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory")),
                fromString("1111-11-11-11-111111"),
                InstructorIdDTO("firstname1", "lastname1")
            )

        every { service.createCourse(any()) } returns expectedCourseDTO

        val result = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location("v1/courses/00001111-0011-0011-0011-000000111111")
            .expectBody(CourseDTO::class.java)
            .returnResult()

        assertThat(expectedCourseDTO).isEqualTo(result.responseBody)
    }

    @Test
    fun `createCourse - create new course with name is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO(
            "",
            mutableListOf(CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory")),
            null,
            InstructorIdDTO("firstname1", "lastname1")
        )

        val result = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(CourseDTO::class.java)
            .returnResult()

        assertThat(courseDTO).isEqualTo(result.responseBody)
    }

    @Test
    fun `createCourse - create new course with category is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("testName", mutableListOf(), null, InstructorIdDTO("firstname1", "lastname1"))

        val result = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(CourseDTO::class.java)
            .returnResult()

        assertThat(courseDTO).isEqualTo(result.responseBody)
    }
}

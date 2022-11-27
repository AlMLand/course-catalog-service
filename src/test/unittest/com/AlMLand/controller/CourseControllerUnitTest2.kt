package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.CourseDTO
import com.AlMLand.dto.enums.Category
import com.AlMLand.dto.enums.Category.DEVELOPMENT
import com.AlMLand.service.CourseService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.stream.Stream

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@WebMvcTest(controllers = [CourseController::class])
class CourseControllerUnitTest2(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {
    @MockBean
    private lateinit var courseService: CourseService

    companion object TestUtil {
        @JvmStatic
        fun getAllCoursesWithParamsNameAndCategory(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "name", DEVELOPMENT, listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), 1, 1),
                    CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "testCategory2")), 2, 1)
                )
            ), Arguments.arguments(
                "name", DEVELOPMENT, listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), 1, 1)
                )
            )
        )

        @JvmStatic
        fun getAllCoursesWithParamCategory(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                DEVELOPMENT, listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), 1, 1),
                    CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "testCategory2")), 2, 1)
                )
            ), Arguments.arguments(
                DEVELOPMENT, listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), 1, 1)
                )
            )
        )

        @JvmStatic
        fun getAllCoursesWithParamName(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "na", listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), 1, 1),
                    CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "testCategory2")), 2, 1)
                )
            ), Arguments.arguments(
                "em1", listOf(
                    CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), 1, 1)
                )
            )
        )
    }

    @Test
    fun `handleAllExceptions - check the controller advice`() {
        val courseId = 1
        `when`(courseService.deleteCourse(courseId)).thenThrow(IllegalArgumentException())

        mockMvc.perform(delete("/v1/courses/{id}", courseId))
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `delete - when successful, than return status 200`() {
        val courseId = 1
        `when`(courseService.deleteCourse(courseId)).thenReturn(true)

        mockMvc.perform(delete("/v1/courses/{id}", courseId))
            .andExpect(status().isOk)
    }

    @Test
    fun `delete - when course not found, than status 404`() {
        val courseId = 1
        `when`(courseService.deleteCourse(courseId)).thenReturn(false)

        mockMvc.perform(delete("/v1/courses/{id}", courseId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `updateCourse - update is successful - status 200, header has location to this course, updated course in body `() {
        val courseId = 1
        val courseDTO =
            CourseDTO("updatedName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)
        val updatedCourseDTO =
            CourseDTO("updatedName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), 1, 1)
        val updatedCourseAsJson = objectMapper.writeValueAsString(updatedCourseDTO)

        `when`(courseService.updateCourses(courseId, courseDTO)).thenReturn(updatedCourseDTO)

        val response = mockMvc.perform(
            put("/v1/courses/{id}", courseId)
                .content(objectMapper.writeValueAsString(courseDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(header().stringValues("Location", "v1/courses/1"))
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(updatedCourseAsJson)
    }

    @Test
    fun `updateCourse - course by id is not found - status 404`() {
        val courseId = 1
        val courseDTO = CourseDTO("name", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)
        val courseDTOAsJson = objectMapper.writeValueAsString(courseDTO)

        `when`(courseService.updateCourses(courseId, courseDTO)).thenReturn(courseDTO)

        val response = mockMvc.perform(
            put("/v1/courses/{id}", courseId)
                .content(courseDTOAsJson)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
            .andReturn().response.contentAsString

        assertThat(courseDTOAsJson).isEqualTo(response)
    }

    @Test
    fun `updateCourse - when name is blank, than status 400, body with the same data`() {
        val courseId = 1
        val courseDTO = CourseDTO("", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)

        val response = mockMvc.perform(
            put("/v1/courses/{id}", courseId)
                .content(objectMapper.writeValueAsString(courseDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo("CourseDTO.name must not be blank")
    }

    @Test
    fun `updateCourse - when category is blank, than status 400, body with the same data`() {
        val courseId = 1
        val courseDTO = CourseDTO("name", listOf(), null, 1)

        val response = mockMvc.perform(
            put("/v1/courses/{id}", courseId)
                .content(objectMapper.writeValueAsString(courseDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo("CourseDTO.category must not be empty")
    }

    @Test
    fun `getAllCourses - when no courses are available, than return list with size 0`() {
        `when`(courseService.findAllCourses(null, null)).thenReturn(listOf())

        val response = mockMvc.perform(
            get("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(listOf<CourseDTO>()))
    }

    @Test
    fun `getAllCourses - return list with size 2`() {
        val expectedResponse = listOf(
            CourseDTO("name1", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory1")), 1, 1),
            CourseDTO("name2", listOf(CourseCategoryDTO(DEVELOPMENT, 2, "testCategory2")), 2, 1)
        )
        `when`(courseService.findAllCourses(null, null)).thenReturn(expectedResponse)

        val actualResponse = mockMvc.perform(
            get("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(actualResponse).isEqualTo(objectMapper.writeValueAsString(expectedResponse))
    }

    @ParameterizedTest
    @MethodSource("getAllCoursesWithParamName")
    fun `getAllCourses with param name - first approach size is 2, second approach size is 1`(
        name: String,
        courses: List<CourseDTO>
    ) {
        `when`(courseService.findAllCourses(name, null)).thenReturn(courses)

        val response = mockMvc.perform(
            get("/v1/courses")
                .param("name", name)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(courses))
    }

    @ParameterizedTest
    @MethodSource("getAllCoursesWithParamCategory")
    fun `getAllCourses with param category - first approach size is 2, second approach size is 1`(
        category: Category,
        courses: List<CourseDTO>
    ) {
        `when`(courseService.findAllCourses(null, category)).thenReturn(courses)

        val response = mockMvc.perform(
            get("/v1/courses")
                .param("category", category.name)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(courses))
    }

    @ParameterizedTest
    @MethodSource("getAllCoursesWithParamsNameAndCategory")
    fun `getAllCourses with params name, category - first approach size is 2, second approach size is 1`(
        name: String,
        category: Category,
        courses: List<CourseDTO>
    ) {
        `when`(courseService.findAllCourses(name, category)).thenReturn(courses)

        val response = mockMvc.perform(
            get("/v1/courses")
                .param("name", name)
                .param("category", category.name)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(courses))
    }

    @Test
    fun `getCourse - get course with id - 1`() {
        val courseId = 1
        val courseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), courseId, 1)
        `when`(courseService.findCourse(courseId)).thenReturn(courseDTO)

        val response = mockMvc.perform(
            get("/v1/courses/{id}", courseId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(courseDTO))
    }

    @Test
    fun `getCourse - get course with id, what is not available in db - status not found`() {
        val courseId = 1
        `when`(courseService.findCourse(courseId)).thenReturn(null)

        val response = mockMvc.perform(
            get("/v1/courses/{id}", courseId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andReturn().response.contentAsString

        assertThat(response.length).isEqualTo(0)
    }

    @Test
    fun `createCourse - create new course, should give back the courseDTO with the same data, status 409, id = null`() {
        val courseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)
        val requestBody = objectMapper.writeValueAsString(courseDTO)

        `when`(courseService.createCourse(courseDTO)).thenReturn(courseDTO)

        val result = mockMvc.perform(
            post("/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict)
            .andReturn()

        assertThat(requestBody).isEqualTo(result.response.contentAsString)
    }

    @Test
    fun `createCourse - create new course, should give back the courseDTO with the same data, status 201, id = 1`() {
        val courseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)
        val expectedCourseDTO =
            CourseDTO("testName", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), 1, 1)
        val expectedRedirectedUrl = "v1/courses/1"
        val expectedLocationName = "Location"
        val expectedLocationValue = "v1/courses/1"
        val requestBody = objectMapper.writeValueAsString(courseDTO)

        `when`(courseService.createCourse(courseDTO)).thenReturn(expectedCourseDTO)

        val response = mockMvc.perform(
            post("/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated)
            .andExpect(redirectedUrl(expectedRedirectedUrl))
            .andExpect(header().stringValues(expectedLocationName, expectedLocationValue))
            .andReturn()

        val responseBody = response.response.contentAsString
        assertThat(objectMapper.writeValueAsString(expectedCourseDTO)).isEqualTo(responseBody)
    }

    @Test
    fun `createCourse - create new course with category is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("testName", listOf(), null, 1)
        val courseAsJson = objectMapper.writeValueAsString(courseDTO)

        val response = mockMvc.perform(
            post("/v1/courses")
                .content(courseAsJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(courseAsJson)
    }

    @Test
    fun `createCourse - create new course with name is blank, should give back the courseDTO with the same data, status 400`() {
        val courseDTO = CourseDTO("", listOf(CourseCategoryDTO(DEVELOPMENT, 1, "testCategory")), null, 1)
        val courseAsJson = objectMapper.writeValueAsString(courseDTO)

        val response = mockMvc.perform(
            post("/v1/courses")
                .content(courseAsJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(courseAsJson)
    }
}
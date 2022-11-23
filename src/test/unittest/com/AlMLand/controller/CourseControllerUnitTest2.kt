package com.AlMLand.controller

import com.AlMLand.dto.CourseDTO
import com.AlMLand.service.CourseService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.`when`
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

    @Test
    fun `getCourseByCategoryLike - when no courses founded than status 204`() {
        val category = "example"

        `when`(courseService.findCourseByNameLike(category)).thenReturn(listOf())

        val response = mockMvc.perform(
            get("/v1/courses/categories/{name}", category)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)
    }

    @ParameterizedTest
    @ValueSource(strings = ["testCa", "Category"])
    fun `getCourseByCategoryLike - when courses founded than status 200`(category: String) {
        val expectedCourses = listOf(
            CourseDTO("testName1", "testCategory1", 1),
            CourseDTO("testName2", "testCategory2", 2)
        )
        val coursesAsJson = objectMapper.writeValueAsString(expectedCourses)

        `when`(courseService.findCourseByNameLike(category)).thenReturn(expectedCourses)

        val response = mockMvc.perform(
            get("/v1/courses/categories/{category}", category)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(coursesAsJson)
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
        val courseDTO = CourseDTO("updatedName", "updatedCategory", null)
        val updatedCourseDTO = CourseDTO("updatedName", "updatedCategory", 1)
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
        val courseDTO = CourseDTO("name", "category", null)
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
        val courseDTO = CourseDTO("", "category", null)

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
        val courseDTO = CourseDTO("name", "", null)

        val response = mockMvc.perform(
            put("/v1/courses/{id}", courseId)
                .content(objectMapper.writeValueAsString(courseDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo("CourseDTO.category must not be blank")
    }

    @Test
    fun `getAllCourses - when no courses are available, than return list with size 0`() {
        `when`(courseService.findAllCourses(null)).thenReturn(listOf())

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
            CourseDTO("name1", "category1", 1),
            CourseDTO("name2", "category2", 2)
        )
        `when`(courseService.findAllCourses(null)).thenReturn(expectedResponse)

        val actualResponse = mockMvc.perform(
            get("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(actualResponse).isEqualTo(objectMapper.writeValueAsString(expectedResponse))
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    fun `getAllCourses with param name - first approach size is 2, second approach size is 1`(
        name: String,
        courses: List<CourseDTO>
    ) {
        `when`(courseService.findAllCourses(name)).thenReturn(courses)

        val response = mockMvc.perform(
            get("/v1/courses")
                .param("name", name)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(courses))
    }

    companion object TestUtil {
        @JvmStatic
        fun getArguments(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "na", listOf(
                    CourseDTO("name1", "category1", 1),
                    CourseDTO("name2", "category2", 2)
                )
            ), Arguments.arguments(
                "em1", listOf(
                    CourseDTO("name1", "category1", 1)
                )
            )
        )
    }

    @Test
    fun `getCourse - get course with id - 1`() {
        val courseId = 1
        val courseDTO = CourseDTO("testName", "testCategory", courseId)
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
        val courseDTO = CourseDTO("testName", "testCategory", null)
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
        val courseDTO = CourseDTO("testName", "testCategory", null)
        val expectedCourseDTO = CourseDTO("testName", "testCategory", 1)
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
        val courseDTO = CourseDTO("testName", "", null)
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
        val courseDTO = CourseDTO("", "testCategory", null)
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
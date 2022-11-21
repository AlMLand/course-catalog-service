package com.AlMLand.controller

import com.AlMLand.dto.CourseDTO
import com.AlMLand.service.CourseService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@AutoConfigureWebTestClient
@WebMvcTest(controllers = [CourseController::class])
class CourseControllerUnitTest2(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {
    @MockBean
    private lateinit var courseService: CourseService

    @Test
    fun `getAllCourses - when no courses are available, than return list with size 0`() {
        `when`(courseService.findAllCourses()).thenReturn(listOf())

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
        `when`(courseService.findAllCourses()).thenReturn(expectedResponse)

        val actualResponse = mockMvc.perform(
            get("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        assertThat(actualResponse).isEqualTo(objectMapper.writeValueAsString(expectedResponse))
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
    fun `addCourse - create new course, should give back the courseDTO with the same data, status 409, id = null`() {
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
    fun `addCourse - create new course, should give back the courseDTO with the same data, status 201, id = 1`() {
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
}
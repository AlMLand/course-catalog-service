package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.enums.Category.DEVELOPMENT
import com.AlMLand.service.CourseCategoryService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import java.util.UUID.fromString

@ActiveProfiles("test")
@WebMvcTest(controllers = [CourseCategoryController::class])
class CourseCategoryControllerUnitTest2 @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var service: CourseCategoryService

    @Test
    fun `createCourseCategory - when success, than status 201, location header is available, response dto is equals to expected tdo`() {
        val courseCategory = CourseCategoryDTO(DEVELOPMENT, null, "test")
        val courseCategoryAsJson = objectMapper.writeValueAsString(courseCategory)
        val createdCourseCategory = CourseCategoryDTO(DEVELOPMENT, fromString("1234-56-78-90-123456"), "test")
        val createdCourseCategoryAsJson = objectMapper.writeValueAsString(createdCourseCategory)

        `when`(service.createCourseCategory(courseCategory)).thenReturn(createdCourseCategory)

        val response = mockMvc.perform(
            post("/v1/categories")
                .content(courseCategoryAsJson)
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(createdCourseCategoryAsJson)
    }

    @Test
    fun `createCourseCategory - when course category is already available in db, than status 409, response dto is equals to request tdo`() {
        val courseCategory = CourseCategoryDTO(DEVELOPMENT, null, "test")
        val courseCategoryAsJson = objectMapper.writeValueAsString(courseCategory)
        `when`(service.createCourseCategory(courseCategory)).thenReturn(courseCategory)

        val response = mockMvc.perform(
            post("/v1/categories")
                .content(courseCategoryAsJson)
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isConflict)
            .andExpect(header().doesNotExist("Location"))
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo(courseCategoryAsJson)
    }

}
package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.enums.Category.DEVELOPMENT
import com.AlMLand.dto.enums.Category.QA
import com.AlMLand.exception.customexceptions.CategoryNotExistsException
import com.AlMLand.service.CourseCategoryService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import java.util.UUID.fromString
import java.util.UUID.randomUUID

@ActiveProfiles("test")
@WebMvcTest(controllers = [CourseCategoryController::class])
class CourseCategoryControllerUnitTest2 @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var service: CourseCategoryService

    @Test
    fun `getIdByCategoryAndDescription - when uuid not founded, than status 404, body is blank`() {
        `when`(service.findIdByCategoryAndDescription(anyString(), anyString())).thenReturn(null)
        val response = mockMvc.perform(
            get("/v1/categories/uuid")
                .param("category", "testCategory")
                .param("description", "testDescription")
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andReturn()
            .response.contentAsString
        assertThat(response).isBlank()
    }

    @Test
    fun `getIdByCategoryAndDescription - when uuid is founded, than status 200, the expected uuid is available`() {
        val uuid = UUID.randomUUID()
        `when`(service.findIdByCategoryAndDescription(anyString(), anyString())).thenReturn(uuid)
        val response = mockMvc.perform(
            get("/v1/categories/uuid")
                .param("category", "testCategory")
                .param("description", "testDescription")
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString
        assertThat(response).isEqualTo(objectMapper.writeValueAsString(uuid))
    }

    @Test
    fun `getIdByCategoryAndDescription - when category name is not valid, than status 400, the expected error message(from GlobalErrorHandler) is available`() {
        val category = "FAIL"
        val errorMessage = "The category $category does not exists"
        val expectedErrorMessage = "CategoryNotExistsException observed: $errorMessage"
        `when`(
            service.findIdByCategoryAndDescription(
                anyString(),
                anyString()
            )
        ).thenThrow(CategoryNotExistsException(errorMessage))
        val response = mockMvc.perform(
            get(
                UriComponentsBuilder.fromUriString("/v1/categories/uuid")
                    .queryParam("category", category)
                    .queryParam("description", "test")
                    .toUriString()
            )
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
            .response.contentAsString
        assertThat(response).isEqualTo(expectedErrorMessage)
    }

    @Test
    fun `getAllCourseCategories without param courseName - should return the expected list, status 200`() {
        val courseCategories = listOf(
            CourseCategoryDTO(DEVELOPMENT, randomUUID()),
            CourseCategoryDTO(QA, randomUUID())
        )

        `when`(service.findAllCourseCategories(null)).thenReturn(courseCategories)
        val response = mockMvc.perform(
            get("/v1/categories")
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString
        assertThat(response).isEqualTo(objectMapper.writeValueAsString(courseCategories))
    }

    @Test
    fun `getAllCourseCategories with param courseName - should return the expected list, status 200`() {
        val courseCategories = listOf(
            CourseCategoryDTO(DEVELOPMENT, randomUUID()),
            CourseCategoryDTO(QA, randomUUID())
        )

        `when`(service.findAllCourseCategories(anyString())).thenReturn(courseCategories)
        val response = mockMvc.perform(
            get("/v1/categories")
                .param("courseName", "Kotlin")
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString
        assertThat(response).isEqualTo(objectMapper.writeValueAsString(courseCategories))
    }

    @Test
    fun `getAllCourseCategories without param courseName - should return the empty list, status 404`() {
        `when`(service.findAllCourseCategories(anyString())).thenReturn(listOf())
        val response = mockMvc.perform(
            get("/v1/categories")
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andReturn()
            .response.contentAsString
        assertThat(response).isEqualTo(objectMapper.writeValueAsString(listOf<CourseCategoryDTO>()))
    }

    @Test
    fun `getAllCourseCategories with param courseName - should return the empty list, status 404`() {
        `when`(service.findAllCourseCategories(anyString())).thenReturn(listOf())
        val response = mockMvc.perform(
            get("/v1/categories")
                .param("courseName", "Kotlin")
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andReturn()
            .response.contentAsString
        assertThat(response).isEqualTo(objectMapper.writeValueAsString(listOf<CourseCategoryDTO>()))
    }

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
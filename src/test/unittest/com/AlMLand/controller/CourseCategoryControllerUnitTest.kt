package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.enums.Category
import com.AlMLand.service.CourseCategoryService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@WebMvcTest(controllers = [CourseCategoryController::class])
class CourseCategoryControllerUnitTest(@Autowired private val webTestClient: WebTestClient) {

    @MockkBean
    private lateinit var courseCategoryService: CourseCategoryService

    @Test
    fun `createCourseCategory - when success, than status 201, location header is available, response dto is equals to expected tdo`() {
        val courseCategoryDTO = CourseCategoryDTO(Category.DEVELOPMENT, null, "test")
        val expectedCourseCategoryDTO = CourseCategoryDTO(Category.DEVELOPMENT, 1, "test")

        every { courseCategoryService.createCourseCategory(any()) } returns expectedCourseCategoryDTO

        val response = webTestClient.post()
            .uri("/v1/categories")
            .bodyValue(courseCategoryDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location("/v1/categories/1")
            .expectBody(CourseCategoryDTO::class.java)
            .returnResult().responseBody

        Assertions.assertThat(response).isEqualTo(expectedCourseCategoryDTO)
    }

    @Test
    fun `createCourseCategory - when course category is already available in db, than status 409, response dto is equals to request tdo`() {
        val courseCategoryDTO = CourseCategoryDTO(Category.DEVELOPMENT, null, "test")

        every { courseCategoryService.createCourseCategory(any()) } returns courseCategoryDTO

        val response = webTestClient.post()
            .uri("/v1/categories")
            .bodyValue(courseCategoryDTO)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody(CourseCategoryDTO::class.java)
            .returnResult().responseBody

        Assertions.assertThat(response).isEqualTo(courseCategoryDTO)
    }

}
package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.enums.Category.DEVELOPMENT
import com.AlMLand.dto.enums.Category.MANAGEMENT
import com.AlMLand.service.CourseCategoryService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.*

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@WebMvcTest(controllers = [CourseCategoryController::class])
class CourseCategoryControllerUnitTest(@Autowired private val webTestClient: WebTestClient) {

    @MockkBean
    private lateinit var service: CourseCategoryService

    @Test
    fun `getAllCourseCategories without param courseName - should return the expected list`() {
        val courseCategoryDTOs = listOf(
            CourseCategoryDTO(DEVELOPMENT, UUID.fromString("1234-56-78-90-123456"), "test1"),
            CourseCategoryDTO(MANAGEMENT, UUID.fromString("0987-65-43-21-098765"), "test2")
        )

        every { service.findAllCourseCategories(null) } returns courseCategoryDTOs

        val response = webTestClient.get()
            .uri(UriComponentsBuilder.fromUri(URI("/v1/categories")).toUriString())
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseCategoryDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(courseCategoryDTOs)
    }

    @Test
    fun `getAllCourseCategories with param courseName - should return the expected list`() {
        val courseCategoryDTOs = listOf(
            CourseCategoryDTO(DEVELOPMENT, UUID.fromString("1234-56-78-90-123456"), "test1")
        )

        every { service.findAllCourseCategories(any()) } returns courseCategoryDTOs
        val uri = UriComponentsBuilder.fromUri(URI("/v1/categories"))
            .queryParam("courseName", "Kotlin").toUriString()

        val response = webTestClient.get()
            .uri(uri)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseCategoryDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(courseCategoryDTOs)
    }

    @Test
    fun `getAllCourseCategories without param courseName - should return the empty list`() {
        every { service.findAllCourseCategories(null) } returns listOf()

        val response = webTestClient.get()
            .uri(UriComponentsBuilder.fromUri(URI("/v1/categories")).toUriString())
            .exchange()
            .expectStatus().isNotFound
            .expectBodyList(CourseCategoryDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(listOf<CourseCategoryDTO>())
    }

    @Test
    fun `getAllCourseCategories with param courseName - should return the empty list`() {
        every { service.findAllCourseCategories(any()) } returns listOf()

        val uri = UriComponentsBuilder.fromUri(URI("/v1/categories"))
            .queryParam("courseName", "Kotlin").toUriString()
        val response = webTestClient.get()
            .uri(uri)
            .exchange()
            .expectStatus().isNotFound
            .expectBodyList(CourseCategoryDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(listOf<CourseCategoryDTO>())
    }

    @Test
    fun `createCourseCategory - when success, than status 201, location header is available, response dto is equals to expected tdo`() {
        val courseCategoryDTO = CourseCategoryDTO(DEVELOPMENT, null, "test")
        val expectedCourseCategoryDTO = CourseCategoryDTO(DEVELOPMENT, UUID.fromString("1234-56-78-90-123456"), "test")

        every { service.createCourseCategory(any()) } returns expectedCourseCategoryDTO

        val response = webTestClient.post()
            .uri("/v1/categories")
            .bodyValue(courseCategoryDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location("/v1/categories/00001234-0056-0078-0090-000000123456")
            .expectBody(CourseCategoryDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(expectedCourseCategoryDTO)
    }

    @Test
    fun `createCourseCategory - when course category is already available in db, than status 409, response dto is equals to request tdo`() {
        val courseCategoryDTO = CourseCategoryDTO(DEVELOPMENT, null, "test")
        every { service.createCourseCategory(any()) } returns courseCategoryDTO

        val response = webTestClient.post()
            .uri("/v1/categories")
            .bodyValue(courseCategoryDTO)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(CONFLICT)
            .expectBody(CourseCategoryDTO::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(courseCategoryDTO)
    }

}
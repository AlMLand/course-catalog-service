package com.AlMLand.controller

import com.AlMLand.service.GreetingService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTest(controllers = [GreetingController::class])
@AutoConfigureWebTestClient
class GreetingControllerUnitTest2 {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var greetingService: GreetingService

    @Test
    fun retrieveGreetingTestWithWebTestClient() {
        val expectedResponseBody = "hallo from default profile, Alex"
        val name = "Alex"

        every { greetingService.retrieveGreeting(name) } returns expectedResponseBody

        val result = webTestClient.get().uri("/v1/greetings/{name}", name)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).returnResult()

        Assertions.assertThat(result.responseBody).isEqualTo(expectedResponseBody)
    }

}

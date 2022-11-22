package com.AlMLand.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@AutoConfigureWebTestClient // for webTestClient: WebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingControllerIntegrationTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun retrieveGreetingTest() {
        val expectedResponseBody = "hallo from default profile, Alex"
        val name = "Alex"
        val result = webTestClient.get()
            .uri("v1/greetings/{name}", name)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()

        Assertions.assertThat(result.responseBody).isEqualTo(expectedResponseBody)
    }

}

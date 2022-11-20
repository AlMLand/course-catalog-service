package com.AlMLand.controller

import com.AlMLand.service.GreetingService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [GreetingController::class])
@AutoConfigureWebTestClient
class GreetingControllerUnitTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var greetingService: GreetingService

    @Test
    fun retrieveGreetingTestWithMockMvc() {
        val expectedResponseBody = "hallo from default profile, Alex"
        val name = "Alex"

        `when`(greetingService.retrieveGreeting(anyString())).thenReturn(expectedResponseBody)

        val result = mockMvc.perform(
            get("/v1/greetings/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
        val responseBodyAsString = result.response.contentAsString

        Assertions.assertThat(responseBodyAsString).isEqualTo(expectedResponseBody)
    }
}

package com.AlMLand.controller

import com.AlMLand.service.GreetingService
import mu.KLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/greetings")
class GreetingController(private val greetingService: GreetingService) {
    companion object : KLogging()

    @GetMapping("{name}")
    fun retrieveGreeting(@PathVariable name: String): String {
        logger.info { "The user with name: $name" }
        return greetingService.retrieveGreeting(name)
    }
}
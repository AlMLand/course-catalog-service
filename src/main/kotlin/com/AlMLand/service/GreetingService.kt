package com.AlMLand.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GreetingService {
    // pri ispolzovanii kotlin nuzhno ekranirovat znak $
    @Value("\${message}")
    lateinit var message: String

    fun retrieveGreeting(name: String) = "$message, $name"
}
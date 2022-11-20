package com.AlMLand

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource

@PropertySource("classpath:application.yml")
@SpringBootApplication
class CourseCatalogServiceApplication

fun main(args: Array<String>) {
    runApplication<CourseCatalogServiceApplication>(*args)
}

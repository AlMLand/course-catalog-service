package com.AlMLand

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
@SpringBootApplication
class CourseCatalogServiceApplication

fun main(args: Array<String>) {
	runApplication<CourseCatalogServiceApplication>(*args)
}

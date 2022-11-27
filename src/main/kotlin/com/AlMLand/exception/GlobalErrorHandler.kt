package com.AlMLand.exception

import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Component
@ControllerAdvice
class GlobalErrorHandler : ResponseEntityExceptionHandler() {
    companion object : KLogging()

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("MethodArgumentNotValidException observed: ${ex.message}", ex)
        val errorMessages = ex.bindingResult.allErrors.map { it.defaultMessage }.joinToString(", ")
        return ResponseEntity.badRequest().body(errorMessages)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleAllExceptions(exception: Exception): ResponseEntity<Any> {
        logger.error("Exception observed: ${exception.message}", exception)
        return ResponseEntity.internalServerError().body(exception.message)
    }

    @ExceptionHandler(value = [InstructorNotValidException::class, CategoryNotValidException::class])
    fun <T : RuntimeException> handleNotPresentOfInstructorOrCategory(exception: T): ResponseEntity<Any> {
        when (exception) {
            is InstructorNotValidException -> logger.error(
                "InstructorNotValidException observed: ${exception.message}", exception
            )

            is CategoryNotValidException -> logger.error(
                "CategoryNotValidException observed: ${exception.message}", exception
            )
        }
        return ResponseEntity.badRequest().body(exception.message)
    }
}

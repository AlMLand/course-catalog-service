package com.AlMLand.exception

import com.AlMLand.exception.customexceptions.CategoryNotExistsException
import com.AlMLand.exception.customexceptions.CategoryNotValidException
import com.AlMLand.exception.customexceptions.CustomExceptionMessage
import com.AlMLand.exception.customexceptions.InstructorNotValidException
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
        val errorMessage = exception.message ?: "no error message available"
        logger.error("Exception observed: $errorMessage", exception)
        return ResponseEntity.internalServerError().body(errorMessage)
    }

    @ExceptionHandler(
        value = [InstructorNotValidException::class, CategoryNotValidException::class,
            CategoryNotExistsException::class]
    )
    fun <T : CustomExceptionMessage> handleNotPresentOfInstructorOrCategory(exception: T): ResponseEntity<Any> {
        val errorMessage = exception.getErrorMessage()
        logger.error(errorMessage, exception)
        return ResponseEntity.badRequest().body(errorMessage)
    }
}

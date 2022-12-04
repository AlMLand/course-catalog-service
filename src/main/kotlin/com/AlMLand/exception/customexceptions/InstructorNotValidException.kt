package com.AlMLand.exception.customexceptions

class InstructorNotValidException(message: String) : CustomExceptionMessage(message) {
    override fun getErrorMessage() = "InstructorNotValidException observed: $message"
}
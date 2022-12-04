package com.AlMLand.exception.customexceptions

class CategoryNotValidException(message: String) : CustomExceptionMessage(message) {
    override fun getErrorMessage() = "CategoryNotValidException observed: $message"
}
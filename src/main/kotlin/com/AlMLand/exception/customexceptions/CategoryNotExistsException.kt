package com.AlMLand.exception.customexceptions

class CategoryNotExistsException(message: String) : CustomExceptionMessage(message) {
    override fun getErrorMessage() = "CategoryNotExistsException observed: $message"
}

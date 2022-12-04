package com.AlMLand.exception.customexceptions

abstract class CustomExceptionMessage(message: String) : RuntimeException(message) {
    abstract fun getErrorMessage(): String
}
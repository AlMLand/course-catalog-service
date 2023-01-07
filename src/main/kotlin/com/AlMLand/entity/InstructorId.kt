package com.AlMLand.entity

import com.AlMLand.converter.DbAttributeEncryptor
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Embeddable

@Embeddable
data class InstructorId(
    @Convert(converter = DbAttributeEncryptor::class)
    @field:Column(name = "first_name", nullable = false, updatable = true)
    val firstName: String,
    @Convert(converter = DbAttributeEncryptor::class)
    @field:Column(name = "last_name", nullable = false, updatable = false)
    val lastName: String
) : Serializable

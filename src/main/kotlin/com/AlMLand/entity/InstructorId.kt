package com.AlMLand.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

private const val mySecretKey = "mySecretKey"

@Embeddable
data class InstructorId(
    @field:Column(name = "first_name", nullable = false, updatable = true)
    val firstName: String,
    @field:Column(name = "last_name", nullable = false, updatable = false)
    val lastName: String
) : Serializable

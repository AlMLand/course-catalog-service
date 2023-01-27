package com.AlMLand.entity

import org.hibernate.annotations.ColumnTransformer
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

private const val mySecretKey = "mySecretKey"

@Embeddable
data class InstructorId(
    @ColumnTransformer(
        read = "pgp_sym_decrypt(first_name, '$mySecretKey')",
        write = "pgp_sym_encrypt(?, '$mySecretKey')"
    )
    @field:Column(name = "first_name", nullable = false, updatable = true)
    val firstName: String,
    @ColumnTransformer(
        read = "pgp_sym_decrypt(last_name, '$mySecretKey')",
        write = "pgp_sym_encrypt(?, '$mySecretKey')"
    )
    @field:Column(name = "last_name", nullable = false, updatable = false)
    val lastName: String
) : Serializable
// CREATE EXTENSION pgcrypto;
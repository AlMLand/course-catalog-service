package com.AlMLand.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class AuditableEntity {
    @get:CreatedDate
    @get:Column(nullable = false, updatable = false)
    lateinit var createDate: LocalDateTime

    @get:LastModifiedDate
    @get:Column(nullable = false, updatable = true)
    lateinit var lastModifiedDate: LocalDateTime
}
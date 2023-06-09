package com.AlMLand.entity

import org.hibernate.envers.Audited
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@Audited
@MappedSuperclass
open class AuditableEntity {
    @get:CreatedDate
    @get:Column(nullable = false, updatable = false)
    lateinit var createDate: LocalDateTime

    @get:LastModifiedDate
    @get:Column(nullable = false, updatable = true)
    lateinit var lastModifiedDate: LocalDateTime

    @CreatedBy
    @Column(nullable = false, updatable = false)
    lateinit var createdBy: String

    @LastModifiedBy
    @Column(nullable = false, updatable = true)
    lateinit var lastModifiedBy: String
}

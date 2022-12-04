package com.AlMLand.entity.view

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Immutable
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Immutable
@Table(name = "courses_view")
data class CourseView(
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "CUSTOM_UUID")
    @GenericGenerator(name = "CUSTOM_UUID", strategy = "uuid2")
    @field:Column(insertable = true, nullable = false, updatable = false, length = 36)
    val id: UUID,
    @field:Column(nullable = false, updatable = true)
    var name: String,
    @field:Column(name = "instructor_firstname", nullable = false, updatable = false)
    val firstName: String,
    @field:Column(name = "instructor_lastname", nullable = false, updatable = false)
    val lastName: String
)

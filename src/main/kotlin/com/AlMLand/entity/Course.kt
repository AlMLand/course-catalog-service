package com.AlMLand.entity

import javax.persistence.*

@Entity
@Table(name = "Courses")
data class Course(
    @field:Column(nullable = false)
    var name: String,
    @field:Column(nullable = false)
    var category: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int?
)
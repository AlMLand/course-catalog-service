package com.AlMLand.entity

import javax.persistence.*

@Entity
@Table(name = "Courses")
data class Course(
    var name: String,
    var category: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int?
)
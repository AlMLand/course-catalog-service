package com.AlMLand.entity

import javax.persistence.*

@Entity
@Table(name = "instructors")
data class Instructor(
    @field:Column(name = "first_name", nullable = false, insertable = true, updatable = true)
    var firstName: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(nullable = false, updatable = false)
    val id: Int?,

    @OneToMany(mappedBy = "instructor", cascade = [CascadeType.ALL], orphanRemoval = true)
    val courses: List<Course> = mutableListOf()
)
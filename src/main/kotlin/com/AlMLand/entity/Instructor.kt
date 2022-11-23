package com.AlMLand.entity

import javax.persistence.*

@Entity
@Table(name = "instructors")
data class Instructor(
    @field:Column(nullable = false)
    var name: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,

    @OneToMany(mappedBy = "instructor", cascade = [CascadeType.ALL], orphanRemoval = true)
    val courses: List<Course> = mutableListOf()
)
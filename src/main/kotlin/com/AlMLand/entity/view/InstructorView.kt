package com.AlMLand.entity.view

import com.AlMLand.entity.InstructorId
import org.hibernate.annotations.Immutable
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Immutable
@Table(name = "instructors_view")
data class InstructorView(
    @EmbeddedId
    @field:Column(nullable = false, updatable = false)
    val instructorId: InstructorId,
)

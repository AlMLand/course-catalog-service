package com.AlMLand.entity.view

import org.hibernate.annotations.Immutable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Immutable
@Table(name = "name_description_distinct")
class CourseNameCategoryDesc(
    @Column(insertable = false, updatable = false)
    var name: String,
    @Column(insertable = false, updatable = false)
    var description: String,
    @Id
    @field:Column(insertable = true, nullable = false, updatable = false, length = 36)
    val id: UUID = UUID.randomUUID(),
)
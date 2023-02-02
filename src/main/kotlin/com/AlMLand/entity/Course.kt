package com.AlMLand.entity

import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.PostLoad
import javax.persistence.Table

@EntityListeners(AuditingEntityListener::class, CourseEntityLoggingListener::class)
@Entity
@Table(name = "courses")
data class Course(
    @field:ColumnTransformer(
        read = "convert_from(pgp_sym_decrypt(name::bytea, 'jRyQ2xiIaghGfHk1')::bytea, 'UTF-8')",
        write = "pgp_sym_encrypt(?, 'jRyQ2xiIaghGfHk1')"
    )
    @field:Column(insertable = true, nullable = false, updatable = true)
    var name: String,

    @ManyToMany(fetch = LAZY)
    @JoinTable(
        name = "course_coursecategory",
        joinColumns = [JoinColumn(name = "course_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "course_category_id", referencedColumnName = "id")]
    )
    @field:Column(nullable = false, updatable = true)
    var categories: MutableList<CourseCategory>,

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "CUSTOM_UUID")
    @GenericGenerator(name = "CUSTOM_UUID", strategy = "uuid2")
    @field:Column(insertable = true, nullable = false, updatable = false, length = 36)
    val id: UUID?,

    @ManyToOne(fetch = LAZY)
    @JoinColumns(
        JoinColumn(
            name = "instructor_firstname",
            referencedColumnName = "first_name",
            insertable = true,
            nullable = false,
            updatable = false
        ),
        JoinColumn(
            name = "instructor_lastname",
            referencedColumnName = "last_name",
            insertable = true,
            nullable = false,
            updatable = false
        )
    )
    var instructor: Instructor
) : AuditableEntity() {
    override fun toString(): String {
        return """
            Course(name: $name, category: $categories, id: $id, instructor: ${instructor.instructorId}),
            created at: $createDate, last modified at: $lastModifiedDate
        """.trimIndent()
    }
}

class CourseEntityLoggingListener {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostLoad
    private fun log(course: Course) {
        logger.info("LOG course name loaded from database: ${course.name}")
    }
}
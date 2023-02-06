package com.AlMLand.entity

import com.AlMLand.converter.DbAttributeEncryptor
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.persistence.FetchType.LAZY

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "courses")
data class Course(
    @field:Convert(converter = DbAttributeEncryptor::class)
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

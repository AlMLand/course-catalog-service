package com.AlMLand.repository

import com.AlMLand.dto.enums.Category.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import java.util.*

@SqlGroup(
    Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
    Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
)
@DataJpaTest
@ActiveProfiles("dev")
class CourseCategoryRepositoryTest(@Autowired private val repository: CourseCategoryRepository) {

    @Test
    fun `findByCategoryAndDescription - get first course category from test data list`() {
        assertEquals(
            UUID.fromString("30303030-3132-3334-2d30-3035362d3030"),
            repository.findIdByCategoryAndDescription(DEVELOPMENT, "description1")
        )
    }

    @Test
    fun `findByCourseName - when search course name, than return list with 2 categories`() {
        val courseCategories = repository.findByCoursesName("courseName2")
        assertTrue(courseCategories.size == 2)
        val categories = courseCategories.map { it.category }
        assertTrue(categories.containsAll(listOf(DEVELOPMENT, MANAGEMENT)))
    }

    @SqlGroup(
        Sql(scripts = ["/db/course-category/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
    )
    @Test
    fun `existsByCategoryAndDescription - approach 1 when exists, than return true - approach 2 when not exists, than return false`() {
        assertTrue(repository.existsByCategoryAndDescription(DEVELOPMENT, "testDescription"))
        assertFalse(repository.existsByCategoryAndDescription(DEVELOPMENT, "shouldFail"))
    }

}

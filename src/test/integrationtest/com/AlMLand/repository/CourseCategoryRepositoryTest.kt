package com.AlMLand.repository

import com.AlMLand.dto.enums.Category.DEVELOPMENT
import com.AlMLand.dto.enums.Category.MANAGEMENT
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup

@SqlGroup(
    Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
    Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
)
@DataJpaTest
@ActiveProfiles("dev")
class CourseCategoryRepositoryTest(@Autowired private val repository: CourseCategoryRepository) {

    @Test
    fun `findByCourseName - `() {
        val courseCategories = repository.findByCourseName("testName2")
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

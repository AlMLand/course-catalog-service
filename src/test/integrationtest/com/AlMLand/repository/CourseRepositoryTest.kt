package com.AlMLand.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup

@DataJpaTest
class CourseRepositoryTest(@Autowired private val courseRepository: CourseRepository) {
    private val name = "testName1"
    private val category = "testCategory1"

    @Test
    fun `existsFirst1ByNameAndCategory - should return - false`() {
        Assertions.assertFalse(courseRepository.existsFirst1ByNameAndCategory(name, category))
    }

    @SqlGroup(
        Sql(
            scripts = ["/db/test-data.sql"],
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        Sql(
            scripts = ["/db/clean-up.sql"],
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
    )
    @Test
    fun `existsFirst1ByNameAndCategory - should return - true`() {
        Assertions.assertTrue(courseRepository.existsFirst1ByNameAndCategory(name, category))
    }

}

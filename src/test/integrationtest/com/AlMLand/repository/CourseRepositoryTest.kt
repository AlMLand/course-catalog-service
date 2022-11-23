package com.AlMLand.repository

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup

@ActiveProfiles("test")
@DataJpaTest
class CourseRepositoryTest(@Autowired private val courseRepository: CourseRepository) {

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
    fun `findByCategoryContainingIgnoreCase - should return list with size 2`() {
        assertTrue(courseRepository.findByCategoryContainingIgnoreCase("tESt").size == 2)
        assertTrue(courseRepository.findByCategoryContainingIgnoreCase("ory2").size == 1)
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
    fun `findByNameContainingIgnoreCase - should return list with size 2`() {
        assertTrue(courseRepository.findByNameContainingIgnoreCase("nAMe").size == 2)
        assertTrue(courseRepository.findByNameContainingIgnoreCase("nAMe2").size == 1)
    }

    @Test
    fun `existsFirst1ByNameAndCategory - should return - false`() {
        assertFalse(courseRepository.existsFirst1ByNameAndCategory("testName1", "testName1"))
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
        assertTrue(courseRepository.existsFirst1ByNameAndCategory("testName1", "testCategory1"))
    }

}

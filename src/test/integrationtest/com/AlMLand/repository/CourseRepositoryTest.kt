package com.AlMLand.repository

import com.AlMLand.dto.enums.Category.DEVELOPMENT
import com.AlMLand.dto.enums.Category.MANAGEMENT
import com.AlMLand.entity.CourseCategory
import com.AlMLand.util.PostgreSQLContainerInitializer
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import java.util.*
import java.util.UUID.fromString

@SqlGroup(
    Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
    Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
class CourseRepositoryTest(@Autowired private val repository: CourseRepository) :
    PostgreSQLContainerInitializer() {

    @Test
    fun `findByNameContainingIgnoreCaseAndCategoriesCategory - first approach size is 2, second approach size is 1`() {
        println("AAAAAAAAAAAAAAAAAAA")
        Thread.sleep(10000000)
        assertTrue(
            repository.findByNameContainingIgnoreCaseAndCategoriesCategory(
                "seNam",
                DEVELOPMENT
            ).size == 2
        )
        assertTrue(
            repository.findByNameContainingIgnoreCaseAndCategoriesCategory(
                "name",
                MANAGEMENT
            ).size == 1
        )
    }

    @Test
    fun `findByCategoriesCategory - should return list with size 2 and 1`() {
        assertTrue(repository.findByCategoriesCategory(DEVELOPMENT).size == 2)
        assertTrue(repository.findByCategoriesCategory(MANAGEMENT).size == 1)
    }

    @Test
    fun `findByNameContainingIgnoreCase - should return list with size 2`() {
        assertTrue(repository.findByNameContainingIgnoreCase("nAMe").size == 2)
        assertTrue(repository.findByNameContainingIgnoreCase("nAMe2").size == 1)
    }

    @Test
    fun `existsFirst1ByNameAndCategoriesIn - should return - false`() {
        assertFalse(
            repository.existsFirst1ByNameAndCategoriesIn(
                "testNameNotAvailable",
                listOf(CourseCategory(DEVELOPMENT, fromString("1234-56-78-90-123456"), "testCategory1"))
            )
        )
    }

    @Test
    fun `existsFirst1ByNameAndCategoriesIn - should return - true`() {
        assertTrue(
            repository.existsFirst1ByNameAndCategoriesIn(
                "courseName1",
                listOf(CourseCategory(DEVELOPMENT, fromString("1234-56-78-90-123456"), "description1"))
            )
        )
    }

}

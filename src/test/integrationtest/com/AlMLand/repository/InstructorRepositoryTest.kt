package com.AlMLand.repository

import com.AlMLand.util.PostgreSQLContainerInitializer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InstructorRepositoryTest(@Autowired private val instructorRepository: InstructorRepository) :
    PostgreSQLContainerInitializer() {

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
    fun `existsByName - when instructor with the given name exists, should return true, the other way false`() {
        assertTrue(instructorRepository.existsByName("testInstructor2"))
        assertFalse(instructorRepository.existsByName("testInstructor99"))
    }

}
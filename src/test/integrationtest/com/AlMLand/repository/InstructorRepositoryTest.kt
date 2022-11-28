package com.AlMLand.repository

import com.AlMLand.util.PostgreSQLContainerInitializer
import org.junit.jupiter.api.Assertions.*
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

@SqlGroup(
    Sql(scripts = ["/db/test-data.sql"], executionPhase = BEFORE_TEST_METHOD),
    Sql(scripts = ["/db/clean-up.sql"], executionPhase = AFTER_TEST_METHOD)
)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
class InstructorRepositoryTest(@Autowired private val repository: InstructorRepository) :
    PostgreSQLContainerInitializer() {

    @Test
    fun `existsByFirstName - when instructor with the given firstname exists, should return true, the other way false`() {
        assertTrue(repository.existsByFirstName("firstname2"))
        assertFalse(repository.existsByFirstName("firstname99"))
    }

}
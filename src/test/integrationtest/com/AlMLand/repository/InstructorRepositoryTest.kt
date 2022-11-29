package com.AlMLand.repository

import com.AlMLand.entity.Instructor
import com.AlMLand.entity.InstructorId
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
import java.util.*

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
    fun `findByInstructorId_FirstNameAndInstructorId_LastName - return searched 1 instructor`() {
        val firstName = "firstName1"
        val lastName = "lastName1"
        val expectedInstructor = Instructor(InstructorId(firstName, lastName))
        assertEquals(
            expectedInstructor,
            repository.findByInstructorId_FirstNameAndInstructorId_LastName(firstName, lastName).get()
        )
    }

    @Test
    fun `findByInstructorId_FirstNameAndInstructorId_LastName - when by searched parameters, no instructor founded, than return empty Optional`() {
        assertTrue(
            repository.findByInstructorId_FirstNameAndInstructorId_LastName(
                "firstNameNotExists",
                "lastNameNotExists"
            ).isEmpty
        )
    }

    @Test
    fun `existsByInstructorId_FirstNameAndInstructorId_LastName - first approach when exists, than true, second approach when not exists, than false`() {
        assertTrue(
            repository.existsByInstructorId_FirstNameAndInstructorId_LastName(
                "firstName2",
                "lastName2"
            )
        )
        assertFalse(
            repository.existsByInstructorId_FirstNameAndInstructorId_LastName(
                "instructorFirstNameNotExists",
                "instructorLastNameNotExists"
            )
        )
    }

    @Test
    fun `existsByInstructorId_FirstName - when instructor with the given firstname exists, should return true, the other way false`() {
        assertTrue(repository.existsByInstructorId_FirstName("firstName1"))
        assertFalse(repository.existsByInstructorId_FirstName("firstNameNotExists"))
    }

}
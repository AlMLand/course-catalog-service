package com.AlMLand.converter

import com.AlMLand.entity.Instructor
import com.AlMLand.entity.InstructorId
import com.AlMLand.repository.InstructorRepository
import com.AlMLand.util.PostgreSQLContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.Executors

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
class DbAttributeEncryptorTest(
    @Autowired private val dbAttributeEncryptor: DbAttributeEncryptor,
    @Autowired private val instructorRepository: InstructorRepository
) : PostgreSQLContainerInitializer() {
    companion object {
        private val executor = Executors.newCachedThreadPool()
    }

    @Test
    fun `multithreading save from 1000 instructors`() {
        val maxInstructorCount = 1000L
        for (index in 1..maxInstructorCount) {
            executor.execute {
                instructorRepository.save(
                    Instructor(
                        InstructorId(
                            "firstName$index",
                            "lastName$index"
                        )
                    )
                )
            }
        }
        Thread.sleep(1000) // wait for all objects in queue by reentrantlock
        instructorRepository.count().let {
            assertThat(it).isEqualTo(maxInstructorCount)
        }
    }
}
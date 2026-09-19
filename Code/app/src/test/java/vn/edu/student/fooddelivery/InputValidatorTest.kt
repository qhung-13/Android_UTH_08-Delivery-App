package vn.edu.student.fooddelivery

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import vn.edu.student.fooddelivery.domain.validation.InputValidator

class InputValidatorTest {

    @Test
    fun `name rejects blank values and accepts visible text`() {
        listOf("", "   ", "\t").forEach { assertFalse(InputValidator.isValidName(it)) }
        assertTrue(InputValidator.isValidName("Nguyễn An"))
    }

    @Test
    fun `phone accepts ten digits beginning with zero`() {
        assertTrue(InputValidator.isValidPhone("0987654321"))
        assertTrue(InputValidator.isValidPhone(" 0987654321 "))
    }

    @Test
    fun `phone rejects invalid formats`() {
        listOf("", "987654321", "09876543210", "1987654321", "09A76543@1")
            .forEach { assertFalse(InputValidator.isValidPhone(it)) }
    }

    @Test
    fun `address enforces trimmed minimum length`() {
        listOf("", "   ", "1234", " 1234 ").forEach { assertFalse(InputValidator.isValidAddress(it)) }
        assertTrue(InputValidator.isValidAddress("12345"))
        assertTrue(InputValidator.isValidAddress(" 12345 "))
    }

    @Test
    fun `weight must be positive`() {
        assertFalse(InputValidator.isValidWeight(-1))
        assertFalse(InputValidator.isValidWeight(0))
        assertTrue(InputValidator.isValidWeight(1))
    }
}

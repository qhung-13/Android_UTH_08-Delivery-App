package vn.edu.student.fooddelivery

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import vn.edu.student.fooddelivery.domain.validation.InputValidator

class InputValidatorTest {
    @Test fun `name must contain non whitespace text`() {
        assertTrue(InputValidator.isValidName("Nguyễn An"))
        assertFalse(InputValidator.isValidName("   "))
    }

    @Test fun `phone accepts trimmed Vietnamese local format`() {
        assertTrue(InputValidator.isValidPhone(" 0901234567 "))
    }

    @Test fun `phone rejects malformed values`() {
        assertFalse(InputValidator.isValidPhone("901234567"))
        assertFalse(InputValidator.isValidPhone("09012A4567"))
        assertFalse(InputValidator.isValidPhone("09012345678"))
    }

    @Test fun `address and weight enforce minimums`() {
        assertTrue(InputValidator.isValidAddress("Số 1 A"))
        assertFalse(InputValidator.isValidAddress(" A "))
        assertTrue(InputValidator.isValidWeight(1))
        assertFalse(InputValidator.isValidWeight(0))
    }
}

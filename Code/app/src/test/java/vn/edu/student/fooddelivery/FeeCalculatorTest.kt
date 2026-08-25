package vn.edu.student.fooddelivery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import vn.edu.student.fooddelivery.domain.util.FeeCalculator

class FeeCalculatorTest {

    @Test
    fun `calculate base fee`() {
        val result = FeeCalculator.calculate(
            distanceKm = 0.0,
            weightGram = 1000
        )

        assertEquals(10_000.0, result, 0.001)
    }

    @Test
    fun `calculate fee by distance`() {
        val result = FeeCalculator.calculate(
            distanceKm = 5.0,
            weightGram = 1000
        )

        assertEquals(25_000.0, result, 0.001)
    }

    @Test
    fun `weight exactly threshold has no extra fee`() {
        val result = FeeCalculator.calculate(
            distanceKm = 5.0,
            weightGram = 2000
        )

        assertEquals(25_000.0, result, 0.001)
    }

    @Test
    fun `weight above threshold adds extra fee`() {
        val result = FeeCalculator.calculate(
            distanceKm = 5.0,
            weightGram = 2001
        )

        assertEquals(30_000.0, result, 0.001)
    }

    @Test
    fun `negative distance throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            FeeCalculator.calculate(
                distanceKm = -1.0,
                weightGram = 1000
            )
        }
    }

    @Test
    fun `zero weight throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            FeeCalculator.calculate(
                distanceKm = 5.0,
                weightGram = 0
            )
        }
    }

    @Test
    fun `negative weight throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            FeeCalculator.calculate(
                distanceKm = 5.0,
                weightGram = -100
            )
        }
    }
}
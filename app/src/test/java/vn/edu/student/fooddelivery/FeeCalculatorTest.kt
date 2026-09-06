package vn.edu.student.fooddelivery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import vn.edu.student.fooddelivery.domain.util.FeeCalculator

class FeeCalculatorTest {
    @Test fun `zero distance charges base fee`() =
        assertEquals(10_000.0, FeeCalculator.calculate(0.0, 1_000), 0.001)

    @Test fun `distance fee follows configured rate`() =
        assertEquals(25_000.0, FeeCalculator.calculate(5.0, 1_000), 0.001)

    @Test fun `weight at threshold has no surcharge`() =
        assertEquals(25_000.0, FeeCalculator.calculate(5.0, 2_000), 0.001)

    @Test fun `weight above threshold adds surcharge`() =
        assertEquals(30_000.0, FeeCalculator.calculate(5.0, 2_001), 0.001)

    @Test fun `breakdown components sum to total`() {
        val result = FeeCalculator.breakdown(5.0, 2_001)
        assertEquals(result.baseFee + result.distanceFee + result.extraWeightFee, result.total, 0.001)
    }

    @Test fun `negative distance is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { FeeCalculator.calculate(-1.0, 1_000) }
    }

    @Test fun `non finite distance is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { FeeCalculator.calculate(Double.NaN, 1_000) }
        assertThrows(IllegalArgumentException::class.java) { FeeCalculator.calculate(Double.POSITIVE_INFINITY, 1_000) }
    }

    @Test fun `zero weight is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { FeeCalculator.calculate(1.0, 0) }
    }

    @Test fun `negative weight is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { FeeCalculator.calculate(1.0, -1) }
    }
}

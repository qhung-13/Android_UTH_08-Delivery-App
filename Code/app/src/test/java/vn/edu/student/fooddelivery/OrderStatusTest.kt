package vn.edu.student.fooddelivery

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.util.OrderStatusValidator

class OrderStatusTest {

    @Test
    fun `valid order status transitions`() {
        assertTrue(
            OrderStatusValidator.canTransition(
                OrderStatus.PENDING,
                OrderStatus.ACCEPTED
            )
        )

        assertTrue(
            OrderStatusValidator.canTransition(
                OrderStatus.ACCEPTED,
                OrderStatus.PICKED_UP
            )
        )

        assertTrue(
            OrderStatusValidator.canTransition(
                OrderStatus.PICKED_UP,
                OrderStatus.IN_TRANSIT
            )
        )

        assertTrue(
            OrderStatusValidator.canTransition(
                OrderStatus.IN_TRANSIT,
                OrderStatus.DELIVERED
            )
        )
    }

    @Test
    fun `valid cancellation transitions`() {
        assertTrue(
            OrderStatusValidator.canTransition(
                OrderStatus.PENDING,
                OrderStatus.CANCELLED
            )
        )

        assertTrue(
            OrderStatusValidator.canTransition(
                OrderStatus.ACCEPTED,
                OrderStatus.CANCELLED
            )
        )
    }

    @Test
    fun `invalid order status transitions`() {
        val valid = setOf(
            OrderStatus.PENDING to OrderStatus.ACCEPTED,
            OrderStatus.PENDING to OrderStatus.CANCELLED,
            OrderStatus.ACCEPTED to OrderStatus.PICKED_UP,
            OrderStatus.ACCEPTED to OrderStatus.CANCELLED,
            OrderStatus.PICKED_UP to OrderStatus.IN_TRANSIT,
            OrderStatus.IN_TRANSIT to OrderStatus.DELIVERED
        )

        for (from in OrderStatus.entries) {
            for (to in OrderStatus.entries) {
                if ((from to to) !in valid) {
                    assertFalse("Unexpected transition: $from -> $to", OrderStatusValidator.canTransition(from, to))
                }
            }
        }
    }

    @Test
    fun `delivered and cancelled are final states`() {
        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.DELIVERED,
                OrderStatus.PENDING
            )
        )

        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.DELIVERED,
                OrderStatus.CANCELLED
            )
        )

        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.CANCELLED,
                OrderStatus.PENDING
            )
        )

        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.CANCELLED,
                OrderStatus.DELIVERED
            )
        )
    }
}

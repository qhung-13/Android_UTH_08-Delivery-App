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
        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.PENDING,
                OrderStatus.DELIVERED
            )
        )

        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.PENDING,
                OrderStatus.IN_TRANSIT
            )
        )

        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.ACCEPTED,
                OrderStatus.DELIVERED
            )
        )

        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.DELIVERED,
                OrderStatus.ACCEPTED
            )
        )

        assertFalse(
            OrderStatusValidator.canTransition(
                OrderStatus.CANCELLED,
                OrderStatus.ACCEPTED
            )
        )
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
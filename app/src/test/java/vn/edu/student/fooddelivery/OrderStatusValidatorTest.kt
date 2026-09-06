package vn.edu.student.fooddelivery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.util.OrderStatusValidator

class OrderStatusValidatorTest {
    @Test fun `all thirty six transitions match the system design table`() {
        val valid = setOf(
            OrderStatus.PENDING to OrderStatus.ACCEPTED,
            OrderStatus.PENDING to OrderStatus.CANCELLED,
            OrderStatus.ACCEPTED to OrderStatus.PICKED_UP,
            OrderStatus.ACCEPTED to OrderStatus.CANCELLED,
            OrderStatus.PICKED_UP to OrderStatus.IN_TRANSIT,
            OrderStatus.IN_TRANSIT to OrderStatus.DELIVERED
        )
        OrderStatus.entries.forEach { from ->
            OrderStatus.entries.forEach { to ->
                assertEquals("Unexpected transition $from -> $to", (from to to) in valid, OrderStatusValidator.canTransition(from, to))
            }
        }
    }

    @Test fun `terminal statuses cannot transition`() {
        OrderStatus.entries.forEach { to ->
            assertFalse(OrderStatusValidator.canTransition(OrderStatus.DELIVERED, to))
            assertFalse(OrderStatusValidator.canTransition(OrderStatus.CANCELLED, to))
        }
    }
}

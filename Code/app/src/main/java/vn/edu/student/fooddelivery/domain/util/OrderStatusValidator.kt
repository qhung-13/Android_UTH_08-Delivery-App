package vn.edu.student.fooddelivery.domain.util

import vn.edu.student.fooddelivery.domain.model.OrderStatus

/**
 * Kiểm soát luồng chuyển trạng thái đơn hàng hợp lệ.
 * PENDING -> ACCEPTED -> PICKED_UP -> IN_TRANSIT -> DELIVERED
 * PENDING/ACCEPTED -> CANCELLED
 * DELIVERED/CANCELLED là trạng thái cuối, không chuyển đi đâu được nữa.
 */
object OrderStatusValidator {

    private val allowedTransitions: Map<OrderStatus, Set<OrderStatus>> = mapOf(
        OrderStatus.PENDING to setOf(OrderStatus.ACCEPTED, OrderStatus.CANCELLED),
        OrderStatus.ACCEPTED to setOf(OrderStatus.PICKED_UP, OrderStatus.CANCELLED),
        OrderStatus.PICKED_UP to setOf(OrderStatus.IN_TRANSIT),
        OrderStatus.IN_TRANSIT to setOf(OrderStatus.DELIVERED),
        OrderStatus.DELIVERED to emptySet(),
        OrderStatus.CANCELLED to emptySet()
    )

    fun canTransition(from: OrderStatus, to: OrderStatus): Boolean {
        return allowedTransitions[from]?.contains(to) == true
    }
}

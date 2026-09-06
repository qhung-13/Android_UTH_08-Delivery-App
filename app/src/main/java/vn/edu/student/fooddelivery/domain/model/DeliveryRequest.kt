package vn.edu.student.fooddelivery.domain.model

enum class OrderStatus {
    PENDING,
    ACCEPTED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}

data class StatusLog(
    val status: OrderStatus,
    val timestamp: Long
)

data class DeliveryRequest(
    val id: String,
    val clientId: String,
    val foodItemId: String,
    val restaurantAddress: String,   // snapshot lúc tạo đơn
    val destinationAddress: String,
    val fee: Double,
    val status: OrderStatus,
    val shipperId: String? = null,
    val createdAt: Long,
    val lastStatusUpdateAt: Long,
    val statusHistory: List<StatusLog> = emptyList()
)

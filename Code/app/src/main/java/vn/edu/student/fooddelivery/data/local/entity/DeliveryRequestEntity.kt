package vn.edu.student.fooddelivery.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "delivery_requests",
    indices = [Index("clientId"), Index("shipperId"), Index("status")]
)
data class DeliveryRequestEntity(
    @PrimaryKey val id: String,
    val clientId: String,
    val foodItemId: String,
    val restaurantAddress: String,
    val destinationAddress: String,
    val fee: Double,
    val status: String,        // enum OrderStatus.name
    val shipperId: String? = null,
    val createdAt: Long,
    val lastStatusUpdateAt: Long
)

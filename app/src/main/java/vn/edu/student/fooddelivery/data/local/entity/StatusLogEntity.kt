package vn.edu.student.fooddelivery.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "status_logs",
    foreignKeys = [
        ForeignKey(
            entity = DeliveryRequestEntity::class,
            parentColumns = ["id"],
            childColumns = ["deliveryRequestId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deliveryRequestId")]
)
data class StatusLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val deliveryRequestId: String,
    val status: String,
    val timestamp: Long
)

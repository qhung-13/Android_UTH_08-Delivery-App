package vn.edu.student.fooddelivery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val role: String   // "CLIENT" hoặc "SHIPPER" — convert qua enum Role ở Mapper
)

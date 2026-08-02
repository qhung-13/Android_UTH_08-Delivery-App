package vn.edu.student.fooddelivery.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_items",
    foreignKeys = [
        ForeignKey(
            entity = RestaurantEntity::class,
            parentColumns = ["id"],
            childColumns = ["restaurantId"]
        )
    ],
    indices = [Index("restaurantId")]
)
data class FoodItemEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val name: String,
    val price: Double,
    val weightGram: Int,
    val imageUrl: String
)

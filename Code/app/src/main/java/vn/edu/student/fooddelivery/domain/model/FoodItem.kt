package vn.edu.student.fooddelivery.domain.model

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val price: Double,
    val weightGram: Int,
    val imageUrl: String
)

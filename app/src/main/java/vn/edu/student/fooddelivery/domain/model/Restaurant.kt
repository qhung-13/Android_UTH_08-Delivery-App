package vn.edu.student.fooddelivery.domain.model

data class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double
)

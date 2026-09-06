package vn.edu.student.fooddelivery.domain.model

enum class Role {
    CLIENT,
    SHIPPER
}

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val role: Role
)

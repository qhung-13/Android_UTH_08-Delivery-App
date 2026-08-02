package vn.edu.student.fooddelivery.data.local

import vn.edu.student.fooddelivery.data.local.entity.DeliveryRequestEntity
import vn.edu.student.fooddelivery.data.local.entity.FoodItemEntity
import vn.edu.student.fooddelivery.data.local.entity.RestaurantEntity
import vn.edu.student.fooddelivery.data.local.entity.StatusLogEntity
import vn.edu.student.fooddelivery.data.local.entity.UserEntity
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.model.Restaurant
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.model.StatusLog
import vn.edu.student.fooddelivery.domain.model.User

// ---------- User ----------
fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    phone = phone,
    role = Role.valueOf(role)
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    phone = phone,
    role = role.name
)

// ---------- Restaurant ----------
fun RestaurantEntity.toDomain() = Restaurant(
    id = id,
    name = name,
    address = address,
    lat = lat,
    lng = lng
)

fun Restaurant.toEntity() = RestaurantEntity(
    id = id,
    name = name,
    address = address,
    lat = lat,
    lng = lng
)

// ---------- FoodItem ----------
fun FoodItemEntity.toDomain() = FoodItem(
    id = id,
    restaurantId = restaurantId,
    name = name,
    price = price,
    weightGram = weightGram,
    imageUrl = imageUrl
)

fun FoodItem.toEntity() = FoodItemEntity(
    id = id,
    restaurantId = restaurantId,
    name = name,
    price = price,
    weightGram = weightGram,
    imageUrl = imageUrl
)

// ---------- DeliveryRequest ----------
fun DeliveryRequestEntity.toDomain(statusHistory: List<StatusLogEntity> = emptyList()) = DeliveryRequest(
    id = id,
    clientId = clientId,
    foodItemId = foodItemId,
    restaurantAddress = restaurantAddress,
    destinationAddress = destinationAddress,
    fee = fee,
    status = OrderStatus.valueOf(status),
    shipperId = shipperId,
    createdAt = createdAt,
    lastStatusUpdateAt = lastStatusUpdateAt,
    statusHistory = statusHistory.map { it.toDomain() }
)

fun DeliveryRequest.toEntity() = DeliveryRequestEntity(
    id = id,
    clientId = clientId,
    foodItemId = foodItemId,
    restaurantAddress = restaurantAddress,
    destinationAddress = destinationAddress,
    fee = fee,
    status = status.name,
    shipperId = shipperId,
    createdAt = createdAt,
    lastStatusUpdateAt = lastStatusUpdateAt
)

fun StatusLogEntity.toDomain() = StatusLog(
    status = OrderStatus.valueOf(status),
    timestamp = timestamp
)
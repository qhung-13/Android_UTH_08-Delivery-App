package vn.edu.student.fooddelivery.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vn.edu.student.fooddelivery.data.local.dao.FoodItemDao
import vn.edu.student.fooddelivery.data.local.dao.RestaurantDao
import vn.edu.student.fooddelivery.data.local.toDomain
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.domain.model.Restaurant

interface FoodRepository {
    fun getAllFoodItems(): Flow<List<FoodItem>>
    suspend fun getFoodItemById(id: String): FoodItem?
    suspend fun getRestaurantById(id: String): Restaurant?
}

class FoodRepositoryImpl(
    private val foodItemDao: FoodItemDao,
    private val restaurantDao: RestaurantDao
) : FoodRepository {

    override fun getAllFoodItems(): Flow<List<FoodItem>> =
        foodItemDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getFoodItemById(id: String): FoodItem? =
        foodItemDao.getById(id)?.toDomain()

    override suspend fun getRestaurantById(id: String): Restaurant? =
        restaurantDao.getById(id)?.toDomain()
}

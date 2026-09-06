package vn.edu.student.fooddelivery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import vn.edu.student.fooddelivery.data.local.entity.FoodItemEntity

@Dao
interface FoodItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items")
    fun getAll(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): FoodItemEntity?

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun count(): Int
}

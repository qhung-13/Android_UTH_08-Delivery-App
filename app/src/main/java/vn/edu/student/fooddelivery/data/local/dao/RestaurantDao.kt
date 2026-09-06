package vn.edu.student.fooddelivery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.edu.student.fooddelivery.data.local.entity.RestaurantEntity

@Dao
interface RestaurantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(restaurants: List<RestaurantEntity>)

    @Query("SELECT * FROM restaurants WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): RestaurantEntity?

    @Query("SELECT COUNT(*) FROM restaurants")
    suspend fun count(): Int
}

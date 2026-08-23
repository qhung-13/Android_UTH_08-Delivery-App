package vn.edu.student.fooddelivery.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vn.edu.student.fooddelivery.data.local.dao.DeliveryRequestDao
import vn.edu.student.fooddelivery.data.local.dao.FoodItemDao
import vn.edu.student.fooddelivery.data.local.dao.RestaurantDao
import vn.edu.student.fooddelivery.data.local.dao.UserDao
import vn.edu.student.fooddelivery.data.local.entity.DeliveryRequestEntity
import vn.edu.student.fooddelivery.data.local.entity.FoodItemEntity
import vn.edu.student.fooddelivery.data.local.entity.RestaurantEntity
import vn.edu.student.fooddelivery.data.local.entity.StatusLogEntity
import vn.edu.student.fooddelivery.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        RestaurantEntity::class,
        FoodItemEntity::class,
        DeliveryRequestEntity::class,
        StatusLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun deliveryRequestDao(): DeliveryRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fooddelivery.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package vn.edu.student.fooddelivery.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
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
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO users(id, name, phone, role)
                    SELECT DISTINCT clientId, 'Tài khoản Client phục hồi', '0000000000', 'CLIENT'
                    FROM delivery_requests
                    WHERE clientId NOT IN (SELECT id FROM users)
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO users(id, name, phone, role)
                    SELECT DISTINCT shipperId, 'Tài khoản Shipper phục hồi', '0000000000', 'SHIPPER'
                    FROM delivery_requests
                    WHERE shipperId IS NOT NULL
                      AND shipperId NOT IN (SELECT id FROM users)
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO restaurants(id, name, address, lat, lng)
                    VALUES ('__recovery_restaurant', 'Dữ liệu phục hồi', 'Không xác định', 0.0, 0.0)
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO food_items(id, restaurantId, name, price, weightGram, imageUrl)
                    SELECT DISTINCT foodItemId, '__recovery_restaurant', 'Món ăn phục hồi', 0.0, 1, ''
                    FROM delivery_requests
                    WHERE foodItemId NOT IN (SELECT id FROM food_items)
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS delivery_requests_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        clientId TEXT NOT NULL,
                        foodItemId TEXT NOT NULL,
                        restaurantAddress TEXT NOT NULL,
                        destinationAddress TEXT NOT NULL,
                        fee REAL NOT NULL,
                        status TEXT NOT NULL,
                        shipperId TEXT,
                        createdAt INTEGER NOT NULL,
                        lastStatusUpdateAt INTEGER NOT NULL,
                        FOREIGN KEY(clientId) REFERENCES users(id) ON UPDATE NO ACTION ON DELETE NO ACTION,
                        FOREIGN KEY(shipperId) REFERENCES users(id) ON UPDATE NO ACTION ON DELETE NO ACTION,
                        FOREIGN KEY(foodItemId) REFERENCES food_items(id) ON UPDATE NO ACTION ON DELETE NO ACTION
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO delivery_requests_new
                    SELECT id, clientId, foodItemId, restaurantAddress, destinationAddress,
                           fee, status, shipperId, createdAt, lastStatusUpdateAt
                    FROM delivery_requests
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE status_logs_backup (
                        logId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        deliveryRequestId TEXT NOT NULL,
                        status TEXT NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO status_logs_backup(logId, deliveryRequestId, status, timestamp)
                    SELECT logId, deliveryRequestId, status, timestamp
                    FROM status_logs
                    WHERE deliveryRequestId IN (SELECT id FROM delivery_requests_new)
                    """.trimIndent()
                )

                // Xóa bảng cũ
                db.execSQL("DROP TABLE status_logs")
                db.execSQL("DROP TABLE delivery_requests")

                // Đổi bảng mới thành tên chính thức
                db.execSQL("ALTER TABLE delivery_requests_new RENAME TO delivery_requests")

                // Tạo lại status_logs với FK trỏ đúng delivery_requests
                db.execSQL(
                    """
                    CREATE TABLE status_logs (
                        logId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        deliveryRequestId TEXT NOT NULL,
                        status TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        FOREIGN KEY(deliveryRequestId) REFERENCES delivery_requests(id)
                            ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

// Khôi phục status logs
                db.execSQL(
                    """
    INSERT INTO status_logs(logId, deliveryRequestId, status, timestamp)
    SELECT logId, deliveryRequestId, status, timestamp
    FROM status_logs_backup
    """.trimIndent()
                )

                db.execSQL("DROP TABLE status_logs_backup")

                db.execSQL("CREATE INDEX IF NOT EXISTS index_delivery_requests_clientId ON delivery_requests(clientId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_delivery_requests_shipperId ON delivery_requests(shipperId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_delivery_requests_foodItemId ON delivery_requests(foodItemId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_delivery_requests_status ON delivery_requests(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_status_logs_deliveryRequestId ON status_logs(deliveryRequestId)")
            }
        }
    }
}

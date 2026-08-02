package vn.edu.student.fooddelivery.data.seed

import vn.edu.student.fooddelivery.data.local.dao.FoodItemDao
import vn.edu.student.fooddelivery.data.local.dao.RestaurantDao
import vn.edu.student.fooddelivery.data.local.entity.FoodItemEntity
import vn.edu.student.fooddelivery.data.local.entity.RestaurantEntity

/**
 * Chèn dữ liệu mẫu Restaurant + FoodItem vào Room khi DB rỗng.
 * Gọi hàm seedIfEmpty() 1 lần trong FoodDeliveryApp.onCreate().
 */
object SeedDataProvider {

    private val restaurants = listOf(
        RestaurantEntity("r1", "Cơm Tấm Sài Gòn", "12 Nguyễn Trãi, Q.1, TP.HCM", 10.7626, 106.6602),
        RestaurantEntity("r2", "Phở Hà Nội 88", "88 Lê Lợi, Q.1, TP.HCM", 10.7720, 106.6980),
        RestaurantEntity("r3", "Bún Chả Hương Liên", "45 Lý Thái Tổ, Q.10, TP.HCM", 10.7680, 106.6670),
        RestaurantEntity("r4", "Trà Sữa Gong Cha", "200 Cách Mạng Tháng 8, Q.3, TP.HCM", 10.7800, 106.6820)
    )

    private val foodItems = listOf(
        FoodItemEntity("f1", "r1", "Cơm Tấm Sườn Bì Chả", 45_000.0, 550, ""),
        FoodItemEntity("f2", "r1", "Cơm Tấm Sườn Nướng", 40_000.0, 500, ""),
        FoodItemEntity("f3", "r1", "Cơm Tấm Đặc Biệt (2 sườn)", 65_000.0, 750, ""),
        FoodItemEntity("f4", "r2", "Phở Bò Tái", 55_000.0, 600, ""),
        FoodItemEntity("f5", "r2", "Phở Gà", 50_000.0, 580, ""),
        FoodItemEntity("f6", "r2", "Phở Đặc Biệt", 70_000.0, 700, ""),
        FoodItemEntity("f7", "r3", "Bún Chả Hà Nội", 48_000.0, 500, ""),
        FoodItemEntity("f8", "r3", "Nem Rán (10 cái)", 35_000.0, 400, ""),
        FoodItemEntity("f9", "r3", "Bún Chả + Nem Combo", 65_000.0, 850, ""),
        FoodItemEntity("f10", "r4", "Trà Sữa Trân Châu Đường Đen", 39_000.0, 500, ""),
        FoodItemEntity("f11", "r4", "Trà Sữa Matcha", 42_000.0, 500, ""),
        FoodItemEntity("f12", "r4", "Hồng Trà Sữa Kem Cheese", 45_000.0, 500, "")
    )

    suspend fun seedIfEmpty(restaurantDao: RestaurantDao, foodItemDao: FoodItemDao) {
        if (restaurantDao.count() == 0) {
            restaurantDao.insertAll(restaurants)
        }
        if (foodItemDao.count() == 0) {
            foodItemDao.insertAll(foodItems)
        }
    }
}

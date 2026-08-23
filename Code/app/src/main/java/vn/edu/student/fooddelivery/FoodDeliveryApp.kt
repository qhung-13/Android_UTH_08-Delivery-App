package vn.edu.student.fooddelivery

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.datastore.SessionManager
import vn.edu.student.fooddelivery.data.local.AppDatabase
import vn.edu.student.fooddelivery.data.repository.DeliveryRepositoryImpl
import vn.edu.student.fooddelivery.data.repository.FoodRepositoryImpl
import vn.edu.student.fooddelivery.data.repository.UserRepositoryImpl
import vn.edu.student.fooddelivery.data.seed.SeedDataProvider

class FoodDeliveryApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var database: AppDatabase
        private set

    lateinit var sessionManager: SessionManager
        private set

    lateinit var userRepository: UserRepositoryImpl
        private set

    lateinit var foodRepository: FoodRepositoryImpl
        private set

    lateinit var deliveryRepository: DeliveryRepositoryImpl
        private set

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getInstance(this)
        sessionManager = SessionManager(this)

        userRepository = UserRepositoryImpl(database.userDao(), sessionManager)
        foodRepository = FoodRepositoryImpl(database.foodItemDao(), database.restaurantDao())
        deliveryRepository = DeliveryRepositoryImpl(database.deliveryRequestDao())

        appScope.launch {
            SeedDataProvider.seedIfEmpty(database.restaurantDao(), database.foodItemDao())
        }
    }
}

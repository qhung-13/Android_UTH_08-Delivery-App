package vn.edu.student.fooddelivery

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import vn.edu.student.fooddelivery.data.local.AppDatabase
import vn.edu.student.fooddelivery.data.local.entity.DeliveryRequestEntity
import vn.edu.student.fooddelivery.data.local.entity.FoodItemEntity
import vn.edu.student.fooddelivery.data.local.entity.RestaurantEntity
import vn.edu.student.fooddelivery.data.local.entity.UserEntity

@RunWith(AndroidJUnit4::class)
class DeliveryRequestDaoInstrumentedTest {
    private lateinit var database: AppDatabase

    @Before fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After fun tearDown() = database.close()

    @Test fun compareAndSetAllowsOnlyOneShipperAndWritesOneAcceptedLog() = runBlocking {
        database.userDao().insert(UserEntity("client", "Client", "0900000000", "CLIENT"))
        database.userDao().insert(UserEntity("shipper-1", "Shipper 1", "0910000000", "SHIPPER"))
        database.userDao().insert(UserEntity("shipper-2", "Shipper 2", "0920000000", "SHIPPER"))
        database.restaurantDao().insertAll(listOf(RestaurantEntity("r", "Quán", "Địa chỉ quán", 0.0, 0.0)))
        database.foodItemDao().insertAll(listOf(FoodItemEntity("f", "r", "Món", 10_000.0, 500, "")))
        val dao = database.deliveryRequestDao()
        dao.insertWithInitialLog(
            DeliveryRequestEntity(
                "order", "client", "f", "Địa chỉ quán", "Địa chỉ khách", 25_000.0,
                "PENDING", null, 1L, 1L
            )
        )

        val results = coroutineScope {
            listOf("shipper-1", "shipper-2").map { shipperId ->
                async {
                    dao.transitionStatusWithLog("order", "PENDING", "ACCEPTED", 2L, shipperId)
                }
            }.awaitAll()
        }

        assertEquals(1, results.count { it })
        assertTrue(dao.getById("order")?.shipperId in setOf("shipper-1", "shipper-2"))
        assertEquals(listOf("PENDING", "ACCEPTED"), dao.getStatusHistory("order").map { it.status })
    }
}

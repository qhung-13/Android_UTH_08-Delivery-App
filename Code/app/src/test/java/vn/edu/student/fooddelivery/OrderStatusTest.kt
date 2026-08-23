package vn.edu.student.fooddelivery

import org.junit.Assert.assertEquals
import org.junit.Test

class OrderStatusTest {

    private fun getNextStatus(currentStatus: String): String {
        return when (currentStatus) {
            "ACCEPTED" -> "PICKED_UP"
            "PICKED_UP" -> "IN_TRANSIT"
            "IN_TRANSIT" -> "DELIVERED"
            else -> currentStatus
        }
    }

    @Test
    fun testOrderStatusTransition() {
        assertEquals("PICKED_UP", getNextStatus("ACCEPTED"))
        assertEquals("IN_TRANSIT", getNextStatus("PICKED_UP"))
        assertEquals("DELIVERED", getNextStatus("IN_TRANSIT"))
    }
}
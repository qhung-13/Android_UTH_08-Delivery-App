package vn.edu.student.fooddelivery.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")

    data object ClientHome : Screen("client_home")
    data object ClientFoodDetail : Screen("client_food_detail/{foodId}") {
        fun createRoute(foodId: String) = "client_food_detail/$foodId"
    }
    data object ClientCreateOrder : Screen("client_create_order/{foodId}") {
        fun createRoute(foodId: String) = "client_create_order/$foodId"
    }
    data object ClientTracking : Screen("client_tracking")
    data object ClientHistory : Screen("client_history")

    data object ShipperAvailable : Screen("shipper_available")
    data object ShipperMyOrders : Screen("shipper_my_orders")
    data object ShipperOrderDetail : Screen("shipper_order_detail/{orderId}") {
        fun createRoute(orderId: String) = "shipper_order_detail/$orderId"
    }

    data object AccountSwitch : Screen("account_switch")
}

package vn.edu.student.fooddelivery.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import vn.edu.student.fooddelivery.FoodDeliveryApp
import vn.edu.student.fooddelivery.auth.AuthViewModel
import vn.edu.student.fooddelivery.auth.ui.AccountSwitchScreen
import vn.edu.student.fooddelivery.auth.ui.LoginScreen
import vn.edu.student.fooddelivery.client.createorder.CreateOrderScreen
import vn.edu.student.fooddelivery.client.createorder.CreateOrderViewModel
import vn.edu.student.fooddelivery.client.fooddetail.FoodDetailScreen
import vn.edu.student.fooddelivery.client.fooddetail.FoodDetailViewModel
import vn.edu.student.fooddelivery.client.history.OrderHistoryScreen
import vn.edu.student.fooddelivery.client.history.OrderHistoryViewModel
import vn.edu.student.fooddelivery.client.home.HomeScreen
import vn.edu.student.fooddelivery.client.home.HomeViewModel
import vn.edu.student.fooddelivery.client.tracking.TrackingScreen
import vn.edu.student.fooddelivery.client.tracking.TrackingViewModel
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.shipper.availablelist.AvailableOrdersScreen
import vn.edu.student.fooddelivery.shipper.availablelist.AvailableOrdersViewModel
import vn.edu.student.fooddelivery.shipper.myorders.MyOrdersScreen
import vn.edu.student.fooddelivery.shipper.myorders.MyOrdersViewModel
import vn.edu.student.fooddelivery.shipper.orderdetail.ShipperOrderDetailScreen
import vn.edu.student.fooddelivery.shipper.orderdetail.ShipperOrderDetailViewModel

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val app = LocalContext.current.applicationContext as FoodDeliveryApp
    val authViewModel: AuthViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AuthViewModel(app.userRepository)
            }
        }
    )
    fun routeForRole(role: Role): String =
        if (role == Role.CLIENT) Screen.ClientHome.route else Screen.ShipperAvailable.route

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        // ---- AUTH ----
        composable(Screen.Login.route) {
//            val authViewModel: AuthViewModel = viewModel(
//                factory = viewModelFactory { initializer { AuthViewModel(app.userRepository) } }
//            )
            val currentUserState by authViewModel.currentUser.collectAsState()

            // Tự động điều hướng khi: (a) mở app đã có session cũ, hoặc (b) vừa đăng ký xong
            LaunchedEffect(currentUserState) {
                val state = currentUserState

                if (state is UiState.Success) {
                    val user = state.data

                    if (user != null) {
                        navController.navigate(routeForRole(user.role)) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }

            LoginScreen(viewModel = authViewModel, onRegisterSuccess = {})
        }

        composable(Screen.AccountSwitch.route) {
            val authViewModel: AuthViewModel = viewModel(
                factory = viewModelFactory { initializer { AuthViewModel(app.userRepository) } }
            )
            AccountSwitchScreen(
                viewModel = authViewModel,
                onAccountSelected = { role ->
                    navController.navigate(routeForRole(role)) { popUpTo(0) }
                },
                onCreateNewAccount = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                }
            )
        }

        // ---- CLIENT ----
        composable(Screen.ClientHome.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = viewModelFactory { initializer { HomeViewModel(app.foodRepository) } }
            )
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToFoodDetail = { foodId ->
                    navController.navigate(Screen.ClientFoodDetail.createRoute(foodId))
                },
                onLogout = {
                    authViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.ClientFoodDetail.route,
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) {
            val detailViewModel: FoodDetailViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { FoodDetailViewModel(createSavedStateHandle(), app.foodRepository) }
                }
            )
            FoodDetailScreen(
                viewModel = detailViewModel,
                onNavigateToCreateOrder = { foodId ->
                    navController.navigate(Screen.ClientCreateOrder.createRoute(foodId))
                }
            )
        }

        composable(
            route = Screen.ClientCreateOrder.route,
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) {
            val createOrderViewModel: CreateOrderViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        CreateOrderViewModel(
                            createSavedStateHandle(),
                            app.foodRepository,
                            app.deliveryRepository,
                            app.userRepository
                        )
                    }
                }
            )
            CreateOrderScreen(
                viewModel = createOrderViewModel,
                onNavigateBackOrTracking = {
                    navController.navigate(Screen.ClientTracking.route) {
                        popUpTo(Screen.ClientHome.route)
                    }
                }
            )
        }

        // ---- CÁC MÀN CHƯA CODE (Người 3 / Người 4) — placeholder tạm ----
        composable(Screen.ClientTracking.route) {
            val trackingViewModel: TrackingViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        TrackingViewModel(
                            deliveryRepository = app.deliveryRepository,
                            userRepository = app.userRepository
                        )
                    }
                }
            )

            TrackingScreen(
                viewModel = trackingViewModel
            )
        }
        composable(Screen.ClientHistory.route) {

            val orderHistoryViewModel: OrderHistoryViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        OrderHistoryViewModel(
                            deliveryRepository = app.deliveryRepository,
                            userRepository = app.userRepository
                        )
                    }
                }
            )

            OrderHistoryScreen(
                viewModel = orderHistoryViewModel
            )
        }
        composable(Screen.ShipperAvailable.route) {

            val availableOrdersViewModel: AvailableOrdersViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        AvailableOrdersViewModel(
                            app.deliveryRepository,
                            app.userRepository
                        )
                    }
                }
            )

            AvailableOrdersScreen(
                viewModel = availableOrdersViewModel,

                onOrderAccepted = {
                    navController.navigate(Screen.ShipperMyOrders.route)
                },

                onLogout = {
                    authViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
        composable(Screen.ShipperMyOrders.route) {

            val myOrdersViewModel: MyOrdersViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        MyOrdersViewModel(
                            app.deliveryRepository,
                            app.userRepository
                        )
                    }
                }
            )

            MyOrdersScreen(
                viewModel = myOrdersViewModel,
                onOrderClick = { orderId ->
                    navController.navigate(
                        Screen.ShipperOrderDetail.createRoute(orderId)
                    )
                }
            )
        }
        composable(
            route = Screen.ShipperOrderDetail.route,
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.StringType
                }
            )
        ) {
            val orderDetailViewModel: ShipperOrderDetailViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        ShipperOrderDetailViewModel(
                            repository = app.deliveryRepository,
                            foodRepository = app.foodRepository,
                            requestId = createSavedStateHandle()["orderId"] ?: ""
                        )
                    }
                }
            )

            val currentUser by app.userRepository
                .getCurrentUser()
                .collectAsState(initial = null)

            ShipperOrderDetailScreen(
                viewModel = orderDetailViewModel,
                shipperId = currentUser?.id ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
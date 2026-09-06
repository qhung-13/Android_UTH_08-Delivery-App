package vn.edu.student.fooddelivery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import vn.edu.student.fooddelivery.domain.model.User
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.shipper.availablelist.AvailableOrdersScreen
import vn.edu.student.fooddelivery.shipper.availablelist.AvailableOrdersViewModel
import vn.edu.student.fooddelivery.shipper.myorders.MyOrdersScreen
import vn.edu.student.fooddelivery.shipper.myorders.MyOrdersViewModel
import vn.edu.student.fooddelivery.shipper.orderdetail.ShipperOrderDetailScreen
import vn.edu.student.fooddelivery.shipper.orderdetail.ShipperOrderDetailViewModel
import vn.edu.student.fooddelivery.ui.components.ErrorState
import vn.edu.student.fooddelivery.ui.components.LoadingIndicator

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val app = LocalContext.current.applicationContext as FoodDeliveryApp
    val authViewModel: AuthViewModel = viewModel(
        factory = viewModelFactory { initializer { AuthViewModel(app.userRepository) } }
    )
    val currentUserState by authViewModel.currentUser.collectAsStateWithLifecycle()

    fun roleRoute(role: Role): String =
        if (role == Role.CLIENT) Screen.ClientHome.route else Screen.ShipperAvailable.route

    fun navigateRoot(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun navigateTab(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun openAccounts() {
        navController.navigate(Screen.AccountSwitch.route) { launchSingleTop = true }
    }

    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LaunchedEffect(currentUserState) {
                val user = (currentUserState as? UiState.Success<User?>)?.data
                if (user != null) navigateRoot(roleRoute(user.role))
            }
            LoginScreen(authViewModel) { navController.navigate(Screen.AccountSwitch.route) }
        }

        composable(Screen.AccountSwitch.route) {
            AccountSwitchScreen(
                viewModel = authViewModel,
                onAccountSelected = { navigateRoot(roleRoute(it)) },
                onCreateNewAccount = {
                    authViewModel.logout()
                    navigateRoot(Screen.Login.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navigateRoot(Screen.Login.route)
                }
            )
        }

        composable(Screen.ClientHome.route) {
            RoleGate(currentUserState, Role.CLIENT, { navigateRoot(Screen.Login.route) }) {
                val vm: HomeViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { HomeViewModel(app.foodRepository, app.userRepository) }
                    }
                )
                HomeScreen(
                    vm,
                    onNavigateToFoodDetail = { navController.navigate(Screen.ClientFoodDetail.createRoute(it)) },
                    onNavigateToTracking = { navigateTab(Screen.ClientTracking.route) },
                    onNavigateToHistory = { navigateTab(Screen.ClientHistory.route) },
                    onAccount = ::openAccounts
                )
            }
        }

        composable(
            Screen.ClientFoodDetail.route,
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) {
            RoleGate(currentUserState, Role.CLIENT, { navigateRoot(Screen.Login.route) }) {
                val vm: FoodDetailViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { FoodDetailViewModel(createSavedStateHandle(), app.foodRepository) }
                    }
                )
                FoodDetailScreen(
                    vm,
                    onBack = { navController.popBackStack() },
                    onNavigateToCreateOrder = { navController.navigate(Screen.ClientCreateOrder.createRoute(it)) }
                )
            }
        }

        composable(
            Screen.ClientCreateOrder.route,
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) {
            RoleGate(currentUserState, Role.CLIENT, { navigateRoot(Screen.Login.route) }) {
                val vm: CreateOrderViewModel = viewModel(
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
                    vm,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navigateRoot(Screen.ClientTracking.route) }
                )
            }
        }

        composable(Screen.ClientTracking.route) {
            RoleGate(currentUserState, Role.CLIENT, { navigateRoot(Screen.Login.route) }) {
                val vm: TrackingViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { TrackingViewModel(app.deliveryRepository, app.userRepository) }
                    }
                )
                TrackingScreen(
                    vm,
                    onNavigateHome = { navigateTab(Screen.ClientHome.route) },
                    onNavigateHistory = { navigateTab(Screen.ClientHistory.route) },
                    onAccount = ::openAccounts
                )
            }
        }

        composable(Screen.ClientHistory.route) {
            RoleGate(currentUserState, Role.CLIENT, { navigateRoot(Screen.Login.route) }) {
                val vm: OrderHistoryViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { OrderHistoryViewModel(app.deliveryRepository, app.userRepository) }
                    }
                )
                OrderHistoryScreen(
                    vm,
                    onNavigateHome = { navigateTab(Screen.ClientHome.route) },
                    onNavigateTracking = { navigateTab(Screen.ClientTracking.route) },
                    onAccount = ::openAccounts
                )
            }
        }

        composable(Screen.ShipperAvailable.route) {
            RoleGate(currentUserState, Role.SHIPPER, { navigateRoot(Screen.Login.route) }) {
                val vm: AvailableOrdersViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { AvailableOrdersViewModel(app.deliveryRepository, app.userRepository) }
                    }
                )
                AvailableOrdersScreen(
                    vm,
                    onOrderAccepted = { navigateTab(Screen.ShipperMyOrders.route) },
                    onNavigateToMyOrders = { navigateTab(Screen.ShipperMyOrders.route) },
                    onAccount = ::openAccounts
                )
            }
        }

        composable(Screen.ShipperMyOrders.route) {
            RoleGate(currentUserState, Role.SHIPPER, { navigateRoot(Screen.Login.route) }) {
                val vm: MyOrdersViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { MyOrdersViewModel(app.deliveryRepository, app.userRepository) }
                    }
                )
                MyOrdersScreen(
                    vm,
                    onOrderClick = { navController.navigate(Screen.ShipperOrderDetail.createRoute(it)) },
                    onNavigateToAvailable = { navigateTab(Screen.ShipperAvailable.route) },
                    onAccount = ::openAccounts
                )
            }
        }

        composable(
            Screen.ShipperOrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            RoleGate(currentUserState, Role.SHIPPER, { navigateRoot(Screen.Login.route) }) {
                val vm: ShipperOrderDetailViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            val savedStateHandle = createSavedStateHandle()
                            ShipperOrderDetailViewModel(
                                app.deliveryRepository,
                                app.foodRepository,
                                app.userRepository,
                                savedStateHandle.get<String>("orderId").orEmpty()
                            )
                        }
                    }
                )
                ShipperOrderDetailScreen(vm, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun RoleGate(
    state: UiState<User?>,
    requiredRole: Role,
    onDenied: () -> Unit,
    content: @Composable () -> Unit
) {
    when (state) {
        UiState.Loading -> LoadingIndicator()
        UiState.Empty -> {
            LaunchedEffect(Unit) { onDenied() }
            LoadingIndicator()
        }
        is UiState.Error -> ErrorState(state.message)
        is UiState.Success -> {
            if (state.data?.role == requiredRole) content()
            else {
                LaunchedEffect(state.data?.id, requiredRole) { onDenied() }
                LoadingIndicator(Modifier)
            }
        }
    }
}

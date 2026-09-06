package vn.edu.student.fooddelivery.client.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.repository.FoodRepository
import vn.edu.student.fooddelivery.data.repository.UserRepository
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.model.User
import vn.edu.student.fooddelivery.domain.util.UiState

data class HomeData(val user: User, val foodItems: List<FoodItem>)

class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init { retry() }

    fun retry() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            combine(
                foodRepository.getAllFoodItems(),
                userRepository.getCurrentUser()
            ) { foods, user ->
                requireNotNull(user) { "Phiên đăng nhập đã hết hạn" }
                require(user.role == Role.CLIENT) { "Tài khoản không có quyền Client" }
                HomeData(user, foods)
            }
                .catch { error -> _uiState.value = UiState.Error(error.message ?: "Không thể tải món ăn") }
                .collect { data -> _uiState.value = UiState.Success(data) }
        }
    }
}

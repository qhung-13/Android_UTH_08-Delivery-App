package vn.edu.student.fooddelivery.client.fooddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.repository.FoodRepository
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.domain.model.Restaurant
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching

data class FoodDetailData(val foodItem: FoodItem, val restaurant: Restaurant)

class FoodDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val foodId = savedStateHandle.get<String>("foodId").orEmpty()
    private val _uiState = MutableStateFlow<UiState<FoodDetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<FoodDetailData>> = _uiState.asStateFlow()

    init { retry() }

    fun retry() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runSuspendCatching {
                require(foodId.isNotBlank()) { "Mã món ăn không hợp lệ" }
                val food = foodRepository.getFoodItemById(foodId) ?: error("Không tìm thấy món ăn")
                val restaurant = foodRepository.getRestaurantById(food.restaurantId)
                    ?: error("Không tìm thấy nhà hàng")
                FoodDetailData(food, restaurant)
            }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Không thể tải món ăn") }
        }
    }
}

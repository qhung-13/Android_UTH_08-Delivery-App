package vn.edu.student.fooddelivery.client.fooddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.domain.model.Restaurant
import vn.edu.student.fooddelivery.data.repository.FoodRepository
import vn.edu.student.fooddelivery.domain.util.UiState

data class FoodDetailData(
    val foodItem: FoodItem,
    val restaurant: Restaurant
)

class FoodDetailViewModel(
    savedStateHandle: SavedStateHandle, // Lấy foodId từ Navigation route
    private val foodRepo: FoodRepository
) : ViewModel() {

    private val foodId: String = checkNotNull(savedStateHandle["foodId"])

    private val _uiState = MutableStateFlow<UiState<FoodDetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<FoodDetailData>> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            try {
                val food = foodRepo.getFoodItemById(foodId)
                if (food != null) {
                    val restaurant = foodRepo.getRestaurantById(food.restaurantId)
                    if (restaurant != null) {
                        _uiState.value = UiState.Success(FoodDetailData(food, restaurant))
                    } else {
                        _uiState.value = UiState.Error("Restaurant not found")
                    }
                } else {
                    _uiState.value = UiState.Error("Food item not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
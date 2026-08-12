package vn.edu.student.fooddelivery.client.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.data.repository.FoodRepository
import vn.edu.student.fooddelivery.domain.util.UiState

class HomeViewModel(
    private val foodRepo: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<FoodItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<FoodItem>>> = _uiState.asStateFlow()

    init {
        loadFoodItems()
    }

    private fun loadFoodItems() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            foodRepo.getAllFoodItems()
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Unknown Error")
                }
                .collect { list ->
                    _uiState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
                }
        }
    }
}
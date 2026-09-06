package vn.edu.student.fooddelivery.client.createorder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.repository.DeliveryRepository
import vn.edu.student.fooddelivery.data.repository.FoodRepository
import vn.edu.student.fooddelivery.data.repository.UserRepository
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.model.Restaurant
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.util.FeeCalculator
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching
import vn.edu.student.fooddelivery.domain.validation.InputValidator
import java.util.UUID

data class CreateOrderData(
    val foodItem: FoodItem,
    val restaurant: Restaurant,
    val fee: FeeCalculator.Breakdown,
    val address: String = "",
    val addressError: String? = null,
    val isSubmitting: Boolean = false
)

class CreateOrderViewModel(
    savedStateHandle: SavedStateHandle,
    private val foodRepository: FoodRepository,
    private val deliveryRepository: DeliveryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val foodId = savedStateHandle.get<String>("foodId").orEmpty()
    private val _uiState = MutableStateFlow<UiState<CreateOrderData>>(UiState.Loading)
    val uiState: StateFlow<UiState<CreateOrderData>> = _uiState.asStateFlow()

    init { retry() }

    fun retry() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runSuspendCatching {
                require(foodId.isNotBlank()) { "Mã món ăn không hợp lệ" }
                val food = foodRepository.getFoodItemById(foodId) ?: error("Không tìm thấy món ăn")
                val restaurant = foodRepository.getRestaurantById(food.restaurantId)
                    ?: error("Không tìm thấy nhà hàng")
                CreateOrderData(food, restaurant, FeeCalculator.breakdown(MOCK_DISTANCE_KM, food.weightGram))
            }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Không thể chuẩn bị đơn") }
        }
    }

    fun onAddressChange(value: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.value = UiState.Success(
            current.copy(
                address = value,
                addressError = if (value.isEmpty() || InputValidator.isValidAddress(value)) null
                else "Địa chỉ phải có ít nhất 5 ký tự"
            )
        )
    }

    fun submit(onSuccess: () -> Unit) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (current.isSubmitting) return
        if (!InputValidator.isValidAddress(current.address)) {
            _uiState.value = UiState.Success(current.copy(addressError = "Địa chỉ phải có ít nhất 5 ký tự"))
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Success(current.copy(isSubmitting = true, addressError = null))
            runSuspendCatching {
                val user = userRepository.getCurrentUser().first() ?: error("Bạn chưa đăng nhập")
                require(user.role == Role.CLIENT) { "Chỉ Client mới được tạo đơn" }
                val now = System.currentTimeMillis()
                val request = DeliveryRequest(
                    id = UUID.randomUUID().toString(),
                    clientId = user.id,
                    foodItemId = current.foodItem.id,
                    restaurantAddress = current.restaurant.address,
                    destinationAddress = current.address.trim(),
                    fee = current.fee.total,
                    status = OrderStatus.PENDING,
                    shipperId = null,
                    createdAt = now,
                    lastStatusUpdateAt = now
                )
                deliveryRepository.createRequest(request).getOrThrow()
            }
                .onSuccess { onSuccess() }
                .onFailure { error -> _uiState.value = UiState.Error(error.message ?: "Tạo đơn thất bại") }
            if (_uiState.value is UiState.Success) {
                _uiState.value = UiState.Success(current.copy(isSubmitting = false))
            }
        }
    }

    companion object { const val MOCK_DISTANCE_KM = 5.0 }
}

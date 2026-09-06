package vn.edu.student.fooddelivery.shipper.orderdetail

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
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching

data class ShipperOrderDetailData(
    val request: DeliveryRequest,
    val foodItem: FoodItem,
    val isUpdating: Boolean = false
)

class ShipperOrderDetailViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository,
    private val requestId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ShipperOrderDetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ShipperOrderDetailData>> = _uiState.asStateFlow()

    init { retry() }

    fun retry() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runSuspendCatching {
                require(requestId.isNotBlank()) { "Mã đơn hàng không hợp lệ" }
                val user = userRepository.getCurrentUser().first() ?: error("Bạn chưa đăng nhập")
                require(user.role == Role.SHIPPER) { "Tài khoản không có quyền Shipper" }
                val request = deliveryRepository.getRequestById(requestId).getOrThrow()
                require(request.shipperId == user.id) { "Đơn hàng không thuộc tài khoản này" }
                val food = foodRepository.getFoodItemById(request.foodItemId) ?: error("Không tìm thấy món ăn")
                ShipperOrderDetailData(request, food)
            }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Không thể tải đơn hàng") }
        }
    }

    fun updateStatus(newStatus: OrderStatus) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (current.isUpdating) return
        viewModelScope.launch {
            _uiState.value = UiState.Success(current.copy(isUpdating = true))
            deliveryRepository.updateStatus(requestId, newStatus)
                .onSuccess { retry() }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Cập nhật trạng thái thất bại") }
        }
    }
}

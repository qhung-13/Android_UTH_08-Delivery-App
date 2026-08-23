package vn.edu.student.fooddelivery.client.ordertracking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import vn.edu.student.fooddelivery.domain.util.UiState

// Tạo tạm một model Đơn hàng để test UI
data class OrderInfo(val id: String, val status: String, val totalAmount: Double)

class OrderTrackingViewModel : ViewModel() {
    // Mặc định vừa tạo đơn xong là PENDING
    private val _uiState = MutableStateFlow<UiState<OrderInfo>>(
        UiState.Success(OrderInfo(id = "ORD-001", status = "PENDING", totalAmount = 150000.0))
    )
    val uiState: StateFlow<UiState<OrderInfo>> = _uiState

    fun cancelOrder() {
        // Chuyển trạng thái sang CANCELLED khi khách hàng bấm hủy
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = UiState.Success(currentState.data.copy(status = "CANCELLED"))
        }
    }
}
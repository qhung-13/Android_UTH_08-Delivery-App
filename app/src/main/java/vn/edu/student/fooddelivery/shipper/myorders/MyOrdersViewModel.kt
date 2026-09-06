package vn.edu.student.fooddelivery.shipper.myorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.repository.DeliveryRepository
import vn.edu.student.fooddelivery.data.repository.UserRepository
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching

data class MyOrdersData(
    val orders: List<DeliveryRequest>,
    val updatingOrderId: String? = null,
    val actionError: String? = null
)

class MyOrdersViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<MyOrdersData>>(UiState.Loading)
    val uiState: StateFlow<UiState<MyOrdersData>> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init { retry() }

    fun retry() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            val user = runSuspendCatching { userRepository.getCurrentUser().first() }
                .getOrElse {
                    _uiState.value = UiState.Error(it.message ?: "Không thể đọc phiên đăng nhập")
                    return@launch
                }
            if (user == null || user.role != Role.SHIPPER) {
                _uiState.value = UiState.Error("Tài khoản không có quyền Shipper")
                return@launch
            }
            deliveryRepository.getRequestsByShipper(user.id)
                .catch { _uiState.value = UiState.Error(it.message ?: "Không thể tải đơn của bạn") }
                .collect { orders ->
                    _uiState.value = if (orders.isEmpty()) UiState.Empty else UiState.Success(MyOrdersData(orders))
                }
        }
    }

    fun updateStatus(orderId: String, newStatus: OrderStatus) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (current.updatingOrderId != null) return
        viewModelScope.launch {
            _uiState.value = UiState.Success(current.copy(updatingOrderId = orderId, actionError = null))
            deliveryRepository.updateStatus(orderId, newStatus)
                .onFailure { error ->
                    _uiState.value = UiState.Success(
                        current.copy(actionError = error.message ?: "Cập nhật trạng thái thất bại")
                    )
                }
        }
    }
}

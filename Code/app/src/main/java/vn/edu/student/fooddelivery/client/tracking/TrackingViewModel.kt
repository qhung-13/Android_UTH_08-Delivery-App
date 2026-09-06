package vn.edu.student.fooddelivery.client.tracking

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

data class TrackingData(
    val orders: List<DeliveryRequest>,
    val busyOrderId: String? = null,
    val actionError: String? = null
)

class TrackingViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<TrackingData>>(UiState.Loading)
    val uiState: StateFlow<UiState<TrackingData>> = _uiState.asStateFlow()
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
            if (user == null || user.role != Role.CLIENT) {
                _uiState.value = UiState.Error("Tài khoản không có quyền Client")
                return@launch
            }
            deliveryRepository.getRequestsByClient(user.id)
                .catch { _uiState.value = UiState.Error(it.message ?: "Không thể tải đơn hàng") }
                .collect { requests ->
                    val active = requests.filterNot { it.status.isTerminal }
                    _uiState.value = if (active.isEmpty()) UiState.Empty else UiState.Success(TrackingData(active))
                }
        }
    }

    fun cancelOrder(orderId: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (current.busyOrderId != null) return
        viewModelScope.launch {
            _uiState.value = UiState.Success(current.copy(busyOrderId = orderId, actionError = null))
            deliveryRepository.cancelRequest(orderId)
                .onFailure { error ->
                    _uiState.value = UiState.Success(
                        current.copy(actionError = error.message ?: "Hủy đơn thất bại")
                    )
                }
        }
    }
}

private val OrderStatus.isTerminal: Boolean
    get() = this == OrderStatus.DELIVERED || this == OrderStatus.CANCELLED

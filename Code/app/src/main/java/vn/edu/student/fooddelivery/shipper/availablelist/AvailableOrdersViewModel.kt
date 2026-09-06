package vn.edu.student.fooddelivery.shipper.availablelist

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
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching

data class AvailableOrdersData(
    val orders: List<DeliveryRequest>,
    val acceptingOrderId: String? = null,
    val actionError: String? = null
)

class AvailableOrdersViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AvailableOrdersData>>(UiState.Loading)
    val uiState: StateFlow<UiState<AvailableOrdersData>> = _uiState.asStateFlow()
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
            deliveryRepository.getPendingRequests()
                .catch { _uiState.value = UiState.Error(it.message ?: "Không thể tải đơn hàng") }
                .collect { orders ->
                    _uiState.value = if (orders.isEmpty()) UiState.Empty
                    else UiState.Success(AvailableOrdersData(orders))
                }
        }
    }

    fun accept(orderId: String, onSuccess: () -> Unit) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (current.acceptingOrderId != null) return
        viewModelScope.launch {
            _uiState.value = UiState.Success(current.copy(acceptingOrderId = orderId, actionError = null))
            val user = runSuspendCatching { userRepository.getCurrentUser().first() }
                .getOrElse {
                    _uiState.value = UiState.Error(it.message ?: "Không thể đọc phiên đăng nhập")
                    return@launch
                }
            if (user == null || user.role != Role.SHIPPER) {
                _uiState.value = UiState.Error("Tài khoản không có quyền Shipper")
                return@launch
            }
            deliveryRepository.acceptRequest(orderId, user.id)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    _uiState.value = UiState.Success(
                        current.copy(actionError = error.message ?: "Không thể nhận đơn")
                    )
                }
        }
    }
}

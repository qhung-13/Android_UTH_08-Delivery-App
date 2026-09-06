package vn.edu.student.fooddelivery.client.history

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

class OrderHistoryViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<DeliveryRequest>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<DeliveryRequest>>> = _uiState.asStateFlow()
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
                .catch { _uiState.value = UiState.Error(it.message ?: "Không thể tải lịch sử") }
                .collect { requests ->
                    val history = requests.filter {
                        it.status == OrderStatus.DELIVERED || it.status == OrderStatus.CANCELLED
                    }
                    _uiState.value = if (history.isEmpty()) UiState.Empty else UiState.Success(history)
                }
        }
    }
}

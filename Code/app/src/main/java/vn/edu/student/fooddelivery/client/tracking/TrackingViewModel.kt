package vn.edu.student.fooddelivery.client.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.repository.DeliveryRepository
import vn.edu.student.fooddelivery.data.repository.UserRepository
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.util.UiState

class TrackingViewModel(
    private val deliveryRepository: DeliveryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<DeliveryRequest>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<DeliveryRequest>>> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Lấy user từ Flow thông qua firstOrNull()
                val user = userRepository.getCurrentUser().firstOrNull()
                if (user != null) {
                    deliveryRepository.getRequestsByClient(user.id)
                        .catch { e ->
                            _uiState.value = UiState.Error(e.message ?: "Có lỗi xảy ra")
                        }
                        .collect { requests ->
                            _uiState.value = if (requests.isEmpty()) {
                                UiState.Empty
                            } else {
                                UiState.Success(requests)
                            }
                        }
                } else {
                    _uiState.value = UiState.Error("Chưa đăng nhập")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Có lỗi xảy ra")
            }
        }
    }
}
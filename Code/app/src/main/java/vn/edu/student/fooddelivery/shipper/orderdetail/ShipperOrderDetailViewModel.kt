package vn.edu.student.fooddelivery.shipper.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.repository.DeliveryRepository
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.model.StatusLog

data class ShipperOrderDetailUiState(
    val isLoading: Boolean = true,
    val request: DeliveryRequest? = null,
    val statusHistory: List<StatusLog> = emptyList(),
    val isUpdating: Boolean = false,
    val error: String? = null
)

class ShipperOrderDetailViewModel(
    private val repository: DeliveryRepository,
    private val requestId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ShipperOrderDetailUiState()
    )

    val uiState: StateFlow<ShipperOrderDetailUiState> =
        _uiState.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val requestResult = repository.getRequestById(requestId)

            requestResult
                .onSuccess { request ->
                    val historyResult =
                        repository.getStatusHistory(requestId)

                    historyResult
                        .onSuccess { history ->
                            _uiState.value = ShipperOrderDetailUiState(
                                isLoading = false,
                                request = request,
                                statusHistory = history
                            )
                        }
                        .onFailure { error ->
                            _uiState.value = ShipperOrderDetailUiState(
                                isLoading = false,
                                request = request,
                                error = error.message
                            )
                        }
                }
                .onFailure { error ->
                    _uiState.value = ShipperOrderDetailUiState(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
    fun acceptOrder(shipperId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUpdating = true,
                error = null
            )

            repository.acceptRequest(
                requestId = requestId,
                shipperId = shipperId
            )
                .onSuccess {
                    loadOrder()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = error.message
                    )
                }
        }
    }
    fun updateStatus(newStatus: OrderStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUpdating = true,
                error = null
            )

            repository.updateStatus(
                requestId = requestId,
                newStatus = newStatus
            )
                .onSuccess {
                    loadOrder()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = error.message
                    )
                }
        }
    }
}
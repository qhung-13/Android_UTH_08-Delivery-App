package vn.edu.student.fooddelivery.shipper.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.repository.DeliveryRepository
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.util.FeeCalculator
import vn.edu.student.fooddelivery.domain.util.UiState
import java.util.UUID

class ShipperTestViewModel(
    private val repository: DeliveryRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<List<DeliveryRequest>>>(UiState.Loading)

    val uiState: StateFlow<UiState<List<DeliveryRequest>>> =
        _uiState.asStateFlow()

    init {
        loadPendingOrders()
    }

    private fun loadPendingOrders() {
        viewModelScope.launch {
            repository.getPendingRequests()
                .catch { e ->
                    _uiState.value =
                        UiState.Error(e.message ?: "Không thể tải đơn hàng")
                }
                .collect { requests ->
                    _uiState.value =
                        if (requests.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(requests)
                        }
                }
        }
    }

    /**
     * CHỈ DÙNG ĐỂ TEST: tự sinh vài đơn PENDING giả, phục vụ test màn
     * ShipperOrderDetail trong lúc màn "Client tạo đơn" (Người 2) và màn
     * "Shipper nhận đơn chính thức" (Người 3) chưa xong.
     *
     * Dùng lại đúng repository.createRequest() (API thật, không mock riêng),
     * nên state machine / statusHistory / atomic transaction vẫn được test
     * đúng như luồng thật. id có prefix "test-" để dễ nhận biết/xoá, và
     * clientId là id giả (không cần user thật trong bảng User vì không có
     * ràng buộc khoá ngoại ở entity).
     */
    fun seedTestData() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val fakeRequests = listOf(
                Triple("f1", "12 Nguyễn Trãi, Q.1, TP.HCM", "45 Điện Biên Phủ, Q.Bình Thạnh"),
                Triple("f4", "88 Lê Lợi, Q.1, TP.HCM", "20 Cộng Hòa, Q.Tân Bình"),
                Triple("f10", "200 Cách Mạng Tháng 8, Q.3, TP.HCM", "10 Phan Xích Long, Q.Phú Nhuận")
            )

            fakeRequests.forEachIndexed { index, (foodItemId, restaurantAddress, destinationAddress) ->
                val request = DeliveryRequest(
                    id = "test-${UUID.randomUUID()}",
                    clientId = "test-client-01",
                    foodItemId = foodItemId,
                    restaurantAddress = restaurantAddress,
                    destinationAddress = destinationAddress,
                    fee = FeeCalculator.calculate(distanceKm = 3.0 + index, weightGram = 500),
                    status = OrderStatus.PENDING,
                    createdAt = now + index,
                    lastStatusUpdateAt = now + index
                )
                repository.createRequest(request)
            }
        }
    }
}
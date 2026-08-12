package vn.edu.student.fooddelivery.client.createorder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.domain.model.*
import vn.edu.student.fooddelivery.data.repository.*
import vn.edu.student.fooddelivery.domain.util.FeeCalculator
import vn.edu.student.fooddelivery.domain.util.UiState
import java.util.UUID

class CreateOrderViewModel(
    savedStateHandle: SavedStateHandle,
    private val foodRepo: FoodRepository,
    private val deliveryRepo: DeliveryRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val foodId: String = checkNotNull(savedStateHandle["foodId"])

    // Lưu tạm dữ liệu món ăn và nhà hàng
    private var currentFood: FoodItem? = null
    private var currentRestaurant: Restaurant? = null

    // State cho UI
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    // State cho form nhập liệu
    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _addressError = MutableStateFlow<String?>(null)
    val addressError: StateFlow<String?> = _addressError.asStateFlow()

    init {
        loadOrderData()
    }

    private fun loadOrderData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                currentFood = foodRepo.getFoodItemById(foodId)
                currentRestaurant = currentFood?.let { foodRepo.getRestaurantById(it.restaurantId) }
                _uiState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error loading data")
            }
        }
    }

    fun onAddressChange(newAddress: String) {
        _address.value = newAddress
        // Validate động khi gõ: Không rỗng, tối thiểu 5 ký tự
        if (newAddress.trim().length < 5) {
            _addressError.value = "Địa chỉ phải có ít nhất 5 ký tự"
        } else {
            _addressError.value = null
        }
    }

    fun submitOrder(onSuccess: () -> Unit) {
        val destAddress = _address.value.trim()

        // Validate bắt buộc trước khi tạo đơn
        if (destAddress.length < 5) {
            _addressError.value = "Địa chỉ phải có ít nhất 5 ký tự"
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = userRepo.getCurrentUser().firstOrNull() ?: throw Exception("User not logged in")
                val food = currentFood ?: throw Exception("Food missing")
                val restaurant = currentRestaurant ?: throw Exception("Restaurant missing")

                // Tính khoảng cách giả lập (do chưa có Google Maps API) - giả sử 5.0 km
                val distanceKm = 5.0

                // Sử dụng công thức tính phí từ FeeCalculator
                val fee = FeeCalculator.calculate(distanceKm, food.weightGram)

                val newRequest = DeliveryRequest(
                    id = UUID.randomUUID().toString(),
                    clientId = user.id,
                    foodItemId = food.id,
                    restaurantAddress = restaurant.address, // Snapshot lúc tạo đơn
                    destinationAddress = destAddress,
                    fee = fee,
                    status = OrderStatus.PENDING, // Trạng thái ban đầu bắt buộc là PENDING
                    shipperId = null,
                    createdAt = System.currentTimeMillis(),
                    lastStatusUpdateAt = System.currentTimeMillis(),
                    statusHistory = emptyList()
                )

                val result = deliveryRepo.createRequest(newRequest)
                if (result.isSuccess) {
                    _uiState.value = UiState.Success(Unit)
                    onSuccess()
                } else {
                    _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Tạo đơn thất bại")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Lỗi hệ thống")
            }
        }
    }
}
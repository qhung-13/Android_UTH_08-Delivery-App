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
import vn.edu.student.fooddelivery.domain.validation.InputValidator
import java.util.UUID

class CreateOrderViewModel(
    savedStateHandle: SavedStateHandle,
    private val foodRepo: FoodRepository,
    private val deliveryRepo: DeliveryRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val foodId: String = checkNotNull(savedStateHandle["foodId"])

    private var currentFood: FoodItem? = null
    private var currentRestaurant: Restaurant? = null

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _addressError = MutableStateFlow<String?>(null)
    val addressError: StateFlow<String?> = _addressError.asStateFlow()

    // ---- MỚI: hiển thị cho người dùng thấy trước khi xác nhận ----
    private val _restaurantAddress = MutableStateFlow("")
    val restaurantAddress: StateFlow<String> = _restaurantAddress.asStateFlow()

    private val _feePreview = MutableStateFlow<Double?>(null)
    val feePreview: StateFlow<Double?> = _feePreview.asStateFlow()
    // ----------------------------------------------------------------

    init {
        loadOrderData()
    }

    private fun loadOrderData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                currentFood = foodRepo.getFoodItemById(foodId)
                currentRestaurant = currentFood?.let { foodRepo.getRestaurantById(it.restaurantId) }

                currentRestaurant?.let { _restaurantAddress.value = it.address }
                currentFood?.let { food ->
                    val distanceKm = 5.0 // TODO: mock, chưa có Maps API — ghi rõ trong báo cáo
                    _feePreview.value = FeeCalculator.calculate(distanceKm, food.weightGram)
                }

                _uiState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error loading data")
            }
        }
    }

    fun onAddressChange(newAddress: String) {
        _address.value = newAddress
        _addressError.value = if (!InputValidator.isValidAddress(newAddress)) {
            "Địa chỉ phải có ít nhất 5 ký tự"
        } else {
            null
        }
    }

    fun submitOrder(onSuccess: () -> Unit) {
        val destAddress = _address.value.trim()

        if (!InputValidator.isValidAddress(destAddress)) {
            _addressError.value = "Địa chỉ phải có ít nhất 5 ký tự"
            return
        }

        val fee = _feePreview.value
        if (fee == null) {
            _uiState.value = UiState.Error("Chưa tính được phí ship, thử lại")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = userRepo.getCurrentUser().firstOrNull() ?: throw Exception("User not logged in")
                val food = currentFood ?: throw Exception("Food missing")
                val restaurant = currentRestaurant ?: throw Exception("Restaurant missing")

                val newRequest = DeliveryRequest(
                    id = UUID.randomUUID().toString(),
                    clientId = user.id,
                    foodItemId = food.id,
                    restaurantAddress = restaurant.address,
                    destinationAddress = destAddress,
                    fee = fee,
                    status = OrderStatus.PENDING,
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
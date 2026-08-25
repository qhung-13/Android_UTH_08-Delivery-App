package vn.edu.student.fooddelivery.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import vn.edu.student.fooddelivery.data.repository.UserRepository
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.model.User
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.domain.validation.InputValidator
import java.util.UUID

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UiState<User?>>(UiState.Loading)
    val currentUser: StateFlow<UiState<User?>> = _currentUser.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _allAccounts = MutableStateFlow<List<User>>(emptyList())
    val allAccounts: StateFlow<List<User>> = _allAccounts.asStateFlow()

    fun refreshAccounts() {
        viewModelScope.launch {
            _allAccounts.value = userRepository.getAllUsers()
        }
    }

    init {
        refreshAccounts()
        userRepository.getCurrentUser()
            .onEach { user -> _currentUser.value = UiState.Success(user) }
            .catch { e -> _currentUser.value = UiState.Error(e.message ?: "Lỗi tải phiên đăng nhập") }
            .launchIn(viewModelScope)
    }

    fun register(name: String, phone: String, role: Role) {
        _registerError.value = null

        if (!InputValidator.isValidName(name)) {
            _registerError.value = "Tên không được để trống"
            return
        }
        if (!InputValidator.isValidPhone(phone)) {
            _registerError.value = "Số điện thoại không hợp lệ (VD: 0901234567)"
            return
        }

        viewModelScope.launch {
            val newUser = User(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                phone = phone.trim(),
                role = role
            )
            userRepository.createUser(newUser)
                .onSuccess { userRepository.setCurrentUser(newUser.id) }
                .onFailure { e -> _registerError.value = e.message ?: "Đăng ký thất bại" }
        }
    }

    fun switchAccount(userId: String) {
        viewModelScope.launch {
            userRepository.setCurrentUser(userId)
        }
    }

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            userRepository.clearCurrentUser()
            onComplete()
        }
    }
}
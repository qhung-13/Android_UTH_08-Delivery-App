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
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching
import vn.edu.student.fooddelivery.domain.validation.InputValidator
import java.util.UUID

data class AuthUiState(
    val accounts: List<User> = emptyList(),
    val isSubmitting: Boolean = false,
    val switchingAccountId: String? = null,
    val error: String? = null
)

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<UiState<User?>>(UiState.Loading)
    val currentUser: StateFlow<UiState<User?>> = _currentUser.asStateFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        userRepository.getCurrentUser()
            .onEach { _currentUser.value = UiState.Success(it) }
            .catch { error -> _currentUser.value = UiState.Error(error.userMessage()) }
            .launchIn(viewModelScope)
        refreshAccounts()
    }

    fun refreshAccounts() {
        viewModelScope.launch {
            runSuspendCatching { userRepository.getAllUsers() }
                .onSuccess { accounts -> _uiState.value = _uiState.value.copy(accounts = accounts) }
                .onFailure { error -> _uiState.value = _uiState.value.copy(error = error.userMessage()) }
        }
    }

    fun register(name: String, phone: String, role: Role) {
        if (_uiState.value.isSubmitting) return
        val validationError = when {
            !InputValidator.isValidName(name) -> "Tên không được để trống"
            !InputValidator.isValidPhone(phone) -> "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0"
            else -> null
        }
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(error = validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null)
            val user = User(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                phone = phone.trim(),
                role = role
            )
            userRepository.createUser(user)
                .onSuccess {
                    runSuspendCatching { userRepository.setCurrentUser(user.id) }
                        .onSuccess {
                            _currentUser.value = UiState.Success(user)
                            refreshAccounts()
                        }
                        .onFailure { error -> _uiState.value = _uiState.value.copy(error = error.userMessage()) }
                }
                .onFailure { error -> _uiState.value = _uiState.value.copy(error = error.userMessage()) }
            _uiState.value = _uiState.value.copy(isSubmitting = false)
        }
    }

    fun switchAccount(userId: String, onSelected: (Role) -> Unit = {}) {
        if (_uiState.value.switchingAccountId != null) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(switchingAccountId = userId, error = null)
            val account = _uiState.value.accounts.firstOrNull { it.id == userId }
            if (account == null) {
                _uiState.value = _uiState.value.copy(error = "Tài khoản không còn tồn tại")
            } else {
                runSuspendCatching { userRepository.setCurrentUser(userId) }
                    .onSuccess {
                        _currentUser.value = UiState.Success(account)
                        onSelected(account.role)
                    }
                    .onFailure { error -> _uiState.value = _uiState.value.copy(error = error.userMessage()) }
            }
            _uiState.value = _uiState.value.copy(switchingAccountId = null)
        }
    }

    fun logout() {
        _currentUser.value = UiState.Success(null)
        viewModelScope.launch {
            runSuspendCatching { userRepository.clearCurrentUser() }
                .onFailure { error -> _uiState.value = _uiState.value.copy(error = error.userMessage()) }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

private fun Throwable.userMessage(): String = message ?: "Đã có lỗi xảy ra"

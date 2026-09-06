package vn.edu.student.fooddelivery.domain.util

/**
 * State chuẩn dùng cho MỌI ViewModel có gọi dữ liệu.
 * Composable luôn phải xử lý đủ 4 nhánh: Loading / Empty / Success / Error.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

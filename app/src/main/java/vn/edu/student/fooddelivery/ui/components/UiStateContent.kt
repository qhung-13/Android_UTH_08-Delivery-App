package vn.edu.student.fooddelivery.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import vn.edu.student.fooddelivery.domain.util.UiState

@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    emptyMessage: String,
    onRetry: (() -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> LoadingIndicator(modifier)
        is UiState.Empty -> EmptyState(message = emptyMessage, modifier = modifier)
        is UiState.Error -> ErrorState(message = state.message, onRetry = onRetry, modifier = modifier)
        is UiState.Success -> content(state.data)
    }
}

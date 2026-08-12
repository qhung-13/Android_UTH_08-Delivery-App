package vn.edu.student.fooddelivery.client.fooddetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.ui.components.*

@Composable
fun FoodDetailScreen(
    viewModel: FoodDetailViewModel,
    onNavigateToCreateOrder: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Empty -> Unit
        is UiState.Error -> ErrorState(message = state.message)
        is UiState.Success -> {
            val data = state.data
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = data.foodItem.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = stringResource(R.string.restaurant_name, data.restaurant.name))
                Text(text = stringResource(R.string.price_format, data.foodItem.price))

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onNavigateToCreateOrder(data.foodItem.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.order_now))
                }
            }
        }
    }
}
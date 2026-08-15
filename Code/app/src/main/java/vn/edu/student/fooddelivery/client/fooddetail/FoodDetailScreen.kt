package vn.edu.student.fooddelivery.client.fooddetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.components.UiStateContent

@Composable
fun FoodDetailScreen(
    viewModel: FoodDetailViewModel,
    onNavigateToCreateOrder: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    UiStateContent(state = uiState) { data ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = data.foodItem.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = stringResource(R.string.restaurant_name, data.restaurant.name))
            Text(text = stringResource(R.string.price_format, data.foodItem.price.toString()))

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = stringResource(R.string.order_now),
                onClick = { onNavigateToCreateOrder(data.foodItem.id) }
            )
        }
    }
}
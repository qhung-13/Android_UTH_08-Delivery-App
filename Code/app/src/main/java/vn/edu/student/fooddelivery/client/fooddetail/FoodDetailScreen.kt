package vn.edu.student.fooddelivery.client.fooddetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.ui.components.DeliveryTopBar
import vn.edu.student.fooddelivery.ui.components.FoodArtwork
import vn.edu.student.fooddelivery.ui.components.InfoRow
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.components.UiStateContent
import vn.edu.student.fooddelivery.ui.formatCurrency
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun FoodDetailScreen(
    viewModel: FoodDetailViewModel,
    onBack: () -> Unit,
    onNavigateToCreateOrder: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { DeliveryTopBar(stringResource(R.string.food_detail), onBack = onBack) }) { padding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(padding),
            emptyMessage = stringResource(R.string.no_data),
            onRetry = viewModel::retry
        ) { data ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(Spacing.large)
            ) {
                FoodArtwork(data.foodItem.name, Modifier.height(220.dp))
                Spacer(Modifier.height(Spacing.xLarge))
                Text(data.foodItem.name, style = MaterialTheme.typography.headlineMedium)
                Text(formatCurrency(data.foodItem.price), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(Spacing.xLarge))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.large)) {
                        InfoRow(stringResource(R.string.restaurant), data.restaurant.name)
                        Spacer(Modifier.height(Spacing.medium))
                        Text(stringResource(R.string.pickup_address), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(data.restaurant.address, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(Spacing.medium))
                        InfoRow(stringResource(R.string.weight), stringResource(R.string.weight_format, data.foodItem.weightGram))
                    }
                }
                Spacer(Modifier.height(Spacing.xLarge))
                PrimaryButton(
                    text = stringResource(R.string.order_now),
                    onClick = { onNavigateToCreateOrder(data.foodItem.id) }
                )
            }
        }
    }
}

package vn.edu.student.fooddelivery.shipper.orderdetail

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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.ui.components.DeliveryTopBar
import vn.edu.student.fooddelivery.ui.components.FoodArtwork
import vn.edu.student.fooddelivery.ui.components.OrderSummaryCard
import vn.edu.student.fooddelivery.ui.components.OrderTimeline
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.components.UiStateContent
import vn.edu.student.fooddelivery.ui.formatCurrency
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun ShipperOrderDetailScreen(viewModel: ShipperOrderDetailViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { DeliveryTopBar(stringResource(R.string.order_detail_title), onBack = onBack) }) { padding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(padding),
            emptyMessage = stringResource(R.string.no_data),
            onRetry = viewModel::retry
        ) { data ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(Spacing.large)
            ) {
                OrderSummaryCard(data.request)
                Spacer(Modifier.height(Spacing.large))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.large)) {
                        FoodArtwork(data.foodItem.name)
                        Spacer(Modifier.height(Spacing.medium))
                        Text(data.foodItem.name, style = MaterialTheme.typography.titleLarge)
                        Text(formatCurrency(data.foodItem.price), color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(Spacing.large))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.large)) {
                        Text(stringResource(R.string.status_timeline), style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(Spacing.medium))
                        OrderTimeline(data.request.statusHistory, stringResource(R.string.no_status_history))
                    }
                }
                nextAction(data.request.status)?.let { (label, status) ->
                    Spacer(Modifier.height(Spacing.xLarge))
                    PrimaryButton(label, { viewModel.updateStatus(status) }, loading = data.isUpdating)
                }
            }
        }
    }
}

@Composable
private fun nextAction(status: OrderStatus): Pair<String, OrderStatus>? = when (status) {
    OrderStatus.ACCEPTED -> stringResource(R.string.start_pickup) to OrderStatus.PICKED_UP
    OrderStatus.PICKED_UP -> stringResource(R.string.start_delivery) to OrderStatus.IN_TRANSIT
    OrderStatus.IN_TRANSIT -> stringResource(R.string.mark_delivered) to OrderStatus.DELIVERED
    else -> null
}

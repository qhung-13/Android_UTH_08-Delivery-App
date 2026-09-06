package vn.edu.student.fooddelivery.shipper.myorders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.ui.components.BottomDestination
import vn.edu.student.fooddelivery.ui.components.DeliveryBottomBar
import vn.edu.student.fooddelivery.ui.components.DeliveryTopBar
import vn.edu.student.fooddelivery.ui.components.OrderSummaryCard
import vn.edu.student.fooddelivery.ui.components.UiStateContent
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun MyOrdersScreen(
    viewModel: MyOrdersViewModel,
    onOrderClick: (String) -> Unit,
    onNavigateToAvailable: () -> Unit,
    onAccount: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { DeliveryTopBar(stringResource(R.string.my_orders_title), accountLabel = stringResource(R.string.account), onAccount = onAccount) },
        bottomBar = {
            DeliveryBottomBar(
                listOf(
                    BottomDestination(stringResource(R.string.nav_available), "＋", false, onNavigateToAvailable),
                    BottomDestination(stringResource(R.string.nav_my_orders), "☷", true) {}
                )
            )
        }
    ) { padding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(padding),
            emptyMessage = stringResource(R.string.empty_my_orders),
            onRetry = viewModel::retry
        ) { data ->
            val active = data.orders.filterNot { it.status.isTerminal }
            val completed = data.orders.filter { it.status.isTerminal }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(Spacing.large),
                verticalArrangement = Arrangement.spacedBy(Spacing.large)
            ) {
                data.actionError?.let { message ->
                    item { Text(message, color = MaterialTheme.colorScheme.error) }
                }
                if (active.isNotEmpty()) {
                    item { SectionTitle(stringResource(R.string.active_orders), active.size) }
                    items(active, key = DeliveryRequest::id) { order ->
                        val action = nextAction(order.status)
                        OrderSummaryCard(
                            order = order,
                            actionLabel = action?.first,
                            actionLoading = data.updatingOrderId == order.id,
                            onAction = action?.second?.let { status -> ({ viewModel.updateStatus(order.id, status) }) },
                            onClick = { onOrderClick(order.id) }
                        )
                    }
                }
                if (completed.isNotEmpty()) {
                    item { SectionTitle(stringResource(R.string.completed_orders), completed.size) }
                    items(completed, key = DeliveryRequest::id) { order ->
                        OrderSummaryCard(order = order, onClick = { onOrderClick(order.id) })
                    }
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

@Composable
private fun SectionTitle(title: String, count: Int) {
    Text("$title · $count", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
}

private val OrderStatus.isTerminal: Boolean
    get() = this == OrderStatus.DELIVERED || this == OrderStatus.CANCELLED

package vn.edu.student.fooddelivery.shipper.availablelist

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
import vn.edu.student.fooddelivery.ui.components.BottomDestination
import vn.edu.student.fooddelivery.ui.components.DeliveryBottomBar
import vn.edu.student.fooddelivery.ui.components.DeliveryTopBar
import vn.edu.student.fooddelivery.ui.components.OrderSummaryCard
import vn.edu.student.fooddelivery.ui.components.UiStateContent
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun AvailableOrdersScreen(
    viewModel: AvailableOrdersViewModel,
    onOrderAccepted: () -> Unit,
    onNavigateToMyOrders: () -> Unit,
    onAccount: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { DeliveryTopBar(stringResource(R.string.available_title), accountLabel = stringResource(R.string.account), onAccount = onAccount) },
        bottomBar = {
            DeliveryBottomBar(
                listOf(
                    BottomDestination(stringResource(R.string.nav_available), "＋", true) {},
                    BottomDestination(stringResource(R.string.nav_my_orders), "☷", false, onNavigateToMyOrders)
                )
            )
        }
    ) { padding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(padding),
            emptyMessage = stringResource(R.string.empty_available),
            onRetry = viewModel::retry
        ) { data ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                data.actionError?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth().padding(Spacing.large)
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(Spacing.large),
                    verticalArrangement = Arrangement.spacedBy(Spacing.large)
                ) {
                    items(data.orders, key = { it.id }) { order ->
                        OrderSummaryCard(
                            order = order,
                            actionLabel = stringResource(R.string.accept_order),
                            actionLoading = data.acceptingOrderId == order.id,
                            onAction = { viewModel.accept(order.id, onOrderAccepted) }
                        )
                    }
                }
            }
        }
    }
}

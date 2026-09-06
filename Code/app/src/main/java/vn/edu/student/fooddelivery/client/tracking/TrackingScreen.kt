package vn.edu.student.fooddelivery.client.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.ui.components.BottomDestination
import vn.edu.student.fooddelivery.ui.components.DeliveryBottomBar
import vn.edu.student.fooddelivery.ui.components.DeliveryTopBar
import vn.edu.student.fooddelivery.ui.components.OrderSummaryCard
import vn.edu.student.fooddelivery.ui.components.OrderTimeline
import vn.edu.student.fooddelivery.ui.components.UiStateContent
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel,
    onNavigateHome: () -> Unit,
    onNavigateHistory: () -> Unit,
    onAccount: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var cancelId by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { DeliveryTopBar(stringResource(R.string.tracking_title), accountLabel = stringResource(R.string.account), onAccount = onAccount) },
        bottomBar = {
            DeliveryBottomBar(
                listOf(
                    BottomDestination(stringResource(R.string.nav_home), "⌂", false, onNavigateHome),
                    BottomDestination(stringResource(R.string.nav_tracking), "◉", true) {},
                    BottomDestination(stringResource(R.string.nav_history), "✓", false, onNavigateHistory)
                )
            )
        }
    ) { padding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(padding),
            emptyMessage = stringResource(R.string.empty_tracking),
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
                        val canCancel = order.status == OrderStatus.PENDING || order.status == OrderStatus.ACCEPTED
                        Column {
                            OrderSummaryCard(
                                order = order,
                                actionLabel = if (canCancel) stringResource(R.string.cancel_order) else null,
                                actionLoading = data.busyOrderId == order.id,
                                onAction = if (canCancel) ({ cancelId = order.id }) else null
                            )
                            Spacer(Modifier.height(Spacing.small))
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(Spacing.large)) {
                                    Text(stringResource(R.string.status_timeline), style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(Spacing.medium))
                                    OrderTimeline(order.statusHistory, stringResource(R.string.no_status_history))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (cancelId != null) {
        AlertDialog(
            onDismissRequest = { cancelId = null },
            title = { Text(stringResource(R.string.cancel_confirm_title)) },
            text = { Text(stringResource(R.string.cancel_confirm_message)) },
            dismissButton = {
                TextButton(onClick = { cancelId = null }) { Text(stringResource(R.string.keep_order)) }
            },
            confirmButton = {
                TextButton(onClick = {
                    cancelId?.let(viewModel::cancelOrder)
                    cancelId = null
                }) { Text(stringResource(R.string.confirm_cancel), color = MaterialTheme.colorScheme.error) }
            }
        )
    }
}

package vn.edu.student.fooddelivery.client.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import vn.edu.student.fooddelivery.ui.components.BottomDestination
import vn.edu.student.fooddelivery.ui.components.DeliveryBottomBar
import vn.edu.student.fooddelivery.ui.components.DeliveryTopBar
import vn.edu.student.fooddelivery.ui.components.OrderSummaryCard
import vn.edu.student.fooddelivery.ui.components.OrderTimeline
import vn.edu.student.fooddelivery.ui.components.UiStateContent
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel,
    onNavigateHome: () -> Unit,
    onNavigateTracking: () -> Unit,
    onAccount: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { DeliveryTopBar(stringResource(R.string.history_title), accountLabel = stringResource(R.string.account), onAccount = onAccount) },
        bottomBar = {
            DeliveryBottomBar(
                listOf(
                    BottomDestination(stringResource(R.string.nav_home), "⌂", false, onNavigateHome),
                    BottomDestination(stringResource(R.string.nav_tracking), "◉", false, onNavigateTracking),
                    BottomDestination(stringResource(R.string.nav_history), "✓", true) {}
                )
            )
        }
    ) { padding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(padding),
            emptyMessage = stringResource(R.string.empty_history),
            onRetry = viewModel::retry
        ) { orders ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(Spacing.large),
                verticalArrangement = Arrangement.spacedBy(Spacing.large)
            ) {
                items(orders, key = { it.id }) { order ->
                    Column {
                        OrderSummaryCard(order)
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

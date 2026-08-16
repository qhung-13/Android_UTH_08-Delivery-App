package vn.edu.student.fooddelivery.client.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.ui.components.StatusBadge
import vn.edu.student.fooddelivery.ui.components.UiStateContent

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    UiStateContent(
        state = uiState,
        emptyMessage = "Chưa có lịch sử đơn hàng",
        onRetry = { viewModel.loadHistory() }
    ) { requests ->
        OrderHistoryList(requests = requests)
    }
}

@Composable
fun OrderHistoryList(requests: List<DeliveryRequest>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(requests) { order ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Đơn #${order.id}")
                    StatusBadge(status = order.status)
                }
            }
        }
    }
}
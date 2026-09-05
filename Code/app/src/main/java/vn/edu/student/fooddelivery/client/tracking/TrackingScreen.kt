package vn.edu.student.fooddelivery.client.tracking

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
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.ui.components.StatusBadge
import vn.edu.student.fooddelivery.ui.components.UiStateContent

@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    UiStateContent(
        state = uiState,
        emptyMessage = "Chưa có đơn hàng nào đang giao",
        onRetry = { viewModel.loadData() }
    ) { requests ->
        OrderTrackingList(
            requests = requests,
            onCancelOrder = { orderId ->
                viewModel.cancelOrder(orderId)
            }
        )
    }
}

@Composable
fun OrderTrackingList(
    requests: List<DeliveryRequest>,
    onCancelOrder: (String) -> Unit
) {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Đơn #${order.id}"
                        )

                        StatusBadge(
                            status = order.status
                        )
                    }

                    // Chỉ cho phép hủy khi PENDING hoặc ACCEPTED
                    if (
                        order.status == OrderStatus.PENDING ||
                        order.status == OrderStatus.ACCEPTED
                    ) {
                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {
                                onCancelOrder(order.id)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Hủy đơn")
                        }
                    }
                }
            }
        }
    }
}
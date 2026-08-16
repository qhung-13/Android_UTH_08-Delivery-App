package vn.edu.student.fooddelivery.shipper.availablelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.components.StatusBadge
import vn.edu.student.fooddelivery.ui.components.UiStateContent

@Composable
fun AvailableOrdersScreen(
    viewModel: AvailableOrdersViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    UiStateContent(
        state = uiState,
        emptyMessage = "Hiện chưa có đơn hàng mới",
        onRetry = { viewModel.loadData() }
    ) { requests ->
        AvailableOrdersList(
            requests = requests,
            onAccept = { orderId -> viewModel.acceptRequest(orderId) }
        )
    }
}

@Composable
fun AvailableOrdersList(
    requests: List<DeliveryRequest>,
    onAccept: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Text(text = "Mã đơn: #${order.id}")
                        StatusBadge(status = order.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryButton(
                        text = "Nhận đơn",
                        onClick = { onAccept(order.id) }
                    )
                }
            }
        }
    }
}
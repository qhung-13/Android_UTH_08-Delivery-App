package vn.edu.student.fooddelivery.shipper.myorders

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
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.components.StatusBadge
import vn.edu.student.fooddelivery.ui.components.UiStateContent

@Composable
fun MyOrdersScreen(
    viewModel: MyOrdersViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    UiStateContent(
        state = uiState,
        emptyMessage = "Bạn chưa nhận đơn hàng nào",
        onRetry = { viewModel.loadData() }
    ) { myOrdersList ->
        MyOrdersList(
            myOrdersList = myOrdersList,
            onUpdateStatus = { orderId, newStatus ->
                viewModel.updateStatus(orderId, newStatus)
            }
        )
    }
}

@Composable
fun MyOrdersList(
    myOrdersList: List<DeliveryRequest>,
    onUpdateStatus: (String, OrderStatus) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(myOrdersList) { order ->
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

                    val (buttonText, nextStatus) = when (order.status) {
                        OrderStatus.ACCEPTED -> "Bắt đầu lấy hàng" to OrderStatus.PICKED_UP
                        OrderStatus.PICKED_UP -> "Bắt đầu giao hàng" to OrderStatus.IN_TRANSIT
                        OrderStatus.IN_TRANSIT -> "Xác nhận đã giao" to OrderStatus.DELIVERED
                        else -> null to null
                    }

                    if (buttonText != null && nextStatus != null) {
                        PrimaryButton(
                            text = buttonText,
                            onClick = { onUpdateStatus(order.id, nextStatus) }
                        )
                    }
                }
            }
        }
    }
}
package vn.edu.student.fooddelivery.shipper.availablelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.components.StatusBadge
import vn.edu.student.fooddelivery.ui.components.UiStateContent

@Composable
fun AvailableOrdersScreen(
    viewModel: AvailableOrdersViewModel,
    onOrderAccepted: () -> Unit,
    onNavigateToMyOrders: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Đơn hàng khả dụng",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            UiStateContent(
                state = uiState,
                emptyMessage = "Hiện chưa có đơn hàng mới",
                onRetry = { viewModel.loadData() }
            ) { requests ->
                AvailableOrdersList(
                    requests = requests,
                    onAccept = { orderId ->
                        viewModel.acceptRequest(
                            orderId = orderId,
                            onSuccess = onOrderAccepted
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tài khoản")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Đơn hàng của tôi")
                    },
                    onClick = {
                        expanded = false
                        onNavigateToMyOrders()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Đăng xuất")
                    },
                    onClick = {
                        expanded = false
                        onLogout()
                    }
                )
            }
        }
    }
}
@Composable
fun AvailableOrdersList(
    requests: List<DeliveryRequest>,
    onAccept: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
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
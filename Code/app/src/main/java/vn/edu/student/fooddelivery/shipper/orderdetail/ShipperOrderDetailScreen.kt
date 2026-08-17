package vn.edu.student.fooddelivery.shipper.orderdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.ui.components.ErrorState
import vn.edu.student.fooddelivery.ui.components.LoadingIndicator

@Composable
fun ShipperOrderDetailScreen(
    viewModel: ShipperOrderDetailViewModel,
    shipperId: String,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            LoadingIndicator()
        }

        uiState.error != null && uiState.request == null -> {
            ErrorState(message = uiState.error!!)
        }

        uiState.request != null -> {
            val request = uiState.request!!

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Chi tiết đơn hàng",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Mã đơn: ${request.id}")
                            Text("Client: ${request.clientId}")
                            Text("Món ăn: ${request.foodItemId}")
                            Text("Điểm lấy: ${request.restaurantAddress}")
                            Text("Điểm giao: ${request.destinationAddress}")
                            Text("Phí ship: ${request.fee} VND")

                            Text(
                                text = "Trạng thái: ${request.status}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Cập nhật trạng thái",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                when (request.status) {

                    OrderStatus.PENDING -> {
                        item {
                            StatusButton(
                                text = "Nhận đơn",
                                enabled = !uiState.isUpdating,
                                onClick = {
                                    viewModel.acceptOrder(shipperId)
                                }
                            )
                        }
                    }

                    OrderStatus.ACCEPTED -> {
                        item {
                            StatusButton(
                                text = "Đã lấy hàng",
                                enabled = !uiState.isUpdating,
                                onClick = {
                                    viewModel.updateStatus(
                                        OrderStatus.PICKED_UP
                                    )
                                }
                            )
                        }
                    }

                    OrderStatus.PICKED_UP -> {
                        item {
                            StatusButton(
                                text = "Bắt đầu giao",
                                enabled = !uiState.isUpdating,
                                onClick = {
                                    viewModel.updateStatus(
                                        OrderStatus.IN_TRANSIT
                                    )
                                }
                            )
                        }
                    }

                    OrderStatus.IN_TRANSIT -> {
                        item {
                            StatusButton(
                                text = "Đã giao hàng",
                                enabled = !uiState.isUpdating,
                                onClick = {
                                    viewModel.updateStatus(
                                        OrderStatus.DELIVERED
                                    )
                                }
                            )
                        }
                    }

                    OrderStatus.DELIVERED -> {
                        item {
                            Text("Đơn hàng đã hoàn tất.")
                        }
                    }

                    OrderStatus.CANCELLED -> {
                        item {
                            Text("Đơn hàng đã bị huỷ.")
                        }
                    }
                }

                if (uiState.error != null) {
                    item {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "Lịch sử trạng thái",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (uiState.statusHistory.isEmpty()) {
                    item {
                        Text("Chưa có lịch sử trạng thái.")
                    }
                } else {
                    items(uiState.statusHistory) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text("Trạng thái: ${log.status}")
                                Text("Thời gian: ${log.timestamp}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}
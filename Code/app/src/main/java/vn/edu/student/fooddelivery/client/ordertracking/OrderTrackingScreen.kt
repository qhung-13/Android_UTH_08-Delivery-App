package vn.edu.student.fooddelivery.client.ordertracking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.domain.util.UiState

@Composable
fun OrderTrackingScreen(
    viewModel: OrderTrackingViewModel,
    onNavigateHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = uiState) {
            is UiState.Success -> {
                val order = state.data

                Text(text = "Mã đơn hàng: ${order.id}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                // Hiển thị trạng thái đơn hàng
                Text(
                    text = "Trạng thái: ${order.status}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (order.status == "PENDING") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(24.dp))

                // CHỈ HIỆN NÚT HỦY KHI ĐANG LÀ PENDING
                if (order.status == "PENDING") {
                    Button(
                        onClick = { viewModel.cancelOrder() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                    ) {
                        Text("Hủy Đơn Hàng")
                    }
                } else {
                    Text(
                        text = "Đơn hàng đã bị hủy, không thể thao tác thêm.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(onClick = onNavigateHome) {
                    Text("Về trang chủ")
                }
            }
            else -> {
                CircularProgressIndicator()
            }
        }
    }
}
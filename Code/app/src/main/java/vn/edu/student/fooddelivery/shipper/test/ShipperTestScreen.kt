package vn.edu.student.fooddelivery.shipper.test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.ui.components.EmptyState
import vn.edu.student.fooddelivery.ui.components.ErrorState
import vn.edu.student.fooddelivery.ui.components.LoadingIndicator
import androidx.compose.foundation.lazy.items
@Composable
fun ShipperTestScreen(
    viewModel: ShipperTestViewModel,
    onOrderClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> {
            LoadingIndicator()
        }

        is UiState.Empty -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EmptyState(message = "Chưa có đơn hàng PENDING")
                SeedTestDataButton(onClick = viewModel::seedTestData)
            }
        }

        is UiState.Error -> {
            ErrorState(message = state.message)
        }

        is UiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Shipper Test",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Các đơn đang chờ nhận",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SeedTestDataButton(onClick = viewModel::seedTestData)

                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(state.data) { request ->
                    OrderCard(
                        request = request,
                        onClick = {
                            onOrderClick(request.id)
                        }
                    )
                }
            }
        }
    }
}

/**
 * CHỈ DÙNG ĐỂ TEST: sinh thêm vài đơn PENDING giả để test màn chi tiết đơn,
 * dùng tạm trong lúc màn "Client tạo đơn" và "Shipper nhận đơn chính thức"
 * của Người 2/Người 3 chưa xong. Nhớ gỡ nút này khi 2 màn đó đã sẵn sàng.
 */
@Composable
private fun SeedTestDataButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("⚠ Tạo đơn PENDING giả để test")
    }
}

@Composable
private fun OrderCard(
    request: DeliveryRequest,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Đơn: ${request.id}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Món: ${request.foodItemId}")
            Text("Điểm lấy: ${request.restaurantAddress}")
            Text("Điểm giao: ${request.destinationAddress}")
            Text("Phí: ${request.fee} VND")
            Text("Trạng thái: ${request.status}")
        }
    }
}
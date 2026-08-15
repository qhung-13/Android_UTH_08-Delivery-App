

package vn.edu.student.fooddelivery.shipper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipperAssignedOrdersScreen() {
    val assignedList = listOf("ORD-2001", "ORD-2002")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Đơn hàng đang giao") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assignedList) { orderId ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Mã đơn: $orderId", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { /* Cập nhật trạng thái */ }) {
                            Text("Xác nhận đã giao")
                        }
                    }
                }
            }
        }
    }
}
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
fun ShipperPendingOrdersScreen() {
    val pendingList = listOf("ORD-5001", "ORD-5002")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Đơn hàng chờ nhận") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pendingList) { orderId ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Mã đơn: $orderId", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { /* Nhận đơn */ }) {
                            Text("Nhận giao đơn này")
                        }
                    }
                }
            }
        }
    }
}


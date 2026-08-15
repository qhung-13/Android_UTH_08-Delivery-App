

package vn.edu.student.fooddelivery.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHistoryScreen() {
    val historyList = listOf("ORD-1001", "ORD-1002", "ORD-1003")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lịch sử đơn hàng") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(historyList) { orderId ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = orderId)
                        Text(text = "Đã hoàn thành", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
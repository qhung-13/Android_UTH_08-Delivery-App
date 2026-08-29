package vn.edu.student.fooddelivery.client.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.ui.components.UiStateContent
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToFoodDetail: (String) -> Unit,
    onNavigateToTracking: () -> Unit,
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
            text = "Trang chủ",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Danh sách món ăn
        Box(
            modifier = Modifier.weight(1f)
        ) {
            UiStateContent(
                state = uiState,
                emptyMessage = stringResource(R.string.empty_food_list)
            ) { foodList ->
                FoodList(
                    foodItems = foodList,
                    onItemClick = { foodId ->
                        onNavigateToFoodDetail(foodId)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nút Tài khoản
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
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Theo dõi đơn hàng")
                    },
                    onClick = {
                        expanded = false
                        onNavigateToTracking()
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
fun FoodList(foodItems: List<FoodItem>, onItemClick: (String) -> Unit,modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(foodItems) { food ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onItemClick(food.id) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = food.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${food.price} VND", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
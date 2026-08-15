package vn.edu.student.fooddelivery.client.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.ui.components.UiStateContent

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToFoodDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    UiStateContent(
        state = uiState,
        emptyMessage = stringResource(R.string.empty_food_list)
    ) { foodList ->
        FoodList(
            foodItems = foodList,
            onItemClick = { foodId -> onNavigateToFoodDetail(foodId) }
        )
    }
}

@Composable
fun FoodList(foodItems: List<FoodItem>, onItemClick: (String) -> Unit) {
    LazyColumn {
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
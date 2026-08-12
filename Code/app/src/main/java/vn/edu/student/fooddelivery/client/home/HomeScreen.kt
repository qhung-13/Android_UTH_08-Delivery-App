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
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.ui.components.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToFoodDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Empty -> EmptyState(message = stringResource(R.string.empty_food_list))
        is UiState.Error -> ErrorState(message = state.message)
        is UiState.Success -> {
            FoodList(
                foodItems = state.data,
                onItemClick = { foodId -> onNavigateToFoodDetail(foodId) }
            )
        }
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

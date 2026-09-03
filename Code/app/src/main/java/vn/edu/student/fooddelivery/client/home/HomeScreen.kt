package vn.edu.student.fooddelivery.client.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.model.FoodItem
import vn.edu.student.fooddelivery.ui.components.BottomDestination
import vn.edu.student.fooddelivery.ui.components.DeliveryBottomBar
import vn.edu.student.fooddelivery.ui.components.DeliveryTopBar
import vn.edu.student.fooddelivery.ui.components.EmptyState
import vn.edu.student.fooddelivery.ui.components.FoodArtwork
import vn.edu.student.fooddelivery.ui.components.UiStateContent
import vn.edu.student.fooddelivery.ui.formatCurrency
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToFoodDetail: (String) -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onAccount: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    val userName = (state as? vn.edu.student.fooddelivery.domain.util.UiState.Success<HomeData>)?.data?.user?.name

    Scaffold(
        topBar = {
            DeliveryTopBar(
                title = stringResource(R.string.home_title),
                accountLabel = userName?.substringBefore(' '),
                onAccount = onAccount
            )
        },
        bottomBar = {
            DeliveryBottomBar(
                listOf(
                    BottomDestination(stringResource(R.string.nav_home), "⌂", true) {},
                    BottomDestination(stringResource(R.string.nav_tracking), "◉", false, onNavigateToTracking),
                    BottomDestination(stringResource(R.string.nav_history), "✓", false, onNavigateToHistory)
                )
            )
        }
    ) { padding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(padding),
            emptyMessage = stringResource(R.string.empty_food_list),
            onRetry = viewModel::retry
        ) { data ->
            val visibleItems = data.foodItems.filter { it.name.contains(query.trim(), ignoreCase = true) }
            Column(Modifier.fillMaxSize().padding(padding)) {
                Text(
                    stringResource(R.string.hello_user, data.user.name),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = Spacing.large)
                )
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(stringResource(R.string.search_food)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(Spacing.large)
                )
                Text(
                    stringResource(R.string.food_count, visibleItems.size),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Spacing.large)
                )
                if (visibleItems.isEmpty()) {
                    EmptyState(stringResource(R.string.empty_search), Modifier.weight(1f))
                } else {
                    FoodGrid(visibleItems, onNavigateToFoodDetail, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FoodGrid(items: List<FoodItem>, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(Spacing.large),
        verticalArrangement = Arrangement.spacedBy(Spacing.large),
        horizontalArrangement = Arrangement.spacedBy(Spacing.large)
    ) {
        items(items, key = FoodItem::id) { food ->
            Card(Modifier.fillMaxWidth().clickable { onClick(food.id) }) {
                Column(Modifier.padding(Spacing.medium)) {
                    FoodArtwork(food.name)
                    Spacer(Modifier.height(Spacing.medium))
                    Text(food.name, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                    Spacer(Modifier.height(Spacing.small))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(formatCurrency(food.price), color = MaterialTheme.colorScheme.primary)
                        Text(stringResource(R.string.weight_format, food.weightGram), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

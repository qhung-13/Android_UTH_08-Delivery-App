package vn.edu.student.fooddelivery.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

data class BottomDestination(
    val label: String,
    val symbol: String,
    val selected: Boolean,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    accountLabel: String? = null,
    onAccount: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (onBack != null) {
                TextButton(onClick = onBack) { Text("‹") }
            }
        },
        actions = {
            if (accountLabel != null && onAccount != null) {
                TextButton(onClick = onAccount) { Text(accountLabel) }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun DeliveryBottomBar(destinations: List<BottomDestination>) {
    NavigationBar {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = destination.selected,
                onClick = destination.onClick,
                icon = { Text(destination.symbol) },
                label = { Text(destination.label) }
            )
        }
    }
}

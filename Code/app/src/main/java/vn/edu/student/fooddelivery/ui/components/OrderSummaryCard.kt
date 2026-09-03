package vn.edu.student.fooddelivery.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.ui.formatCurrency
import vn.edu.student.fooddelivery.ui.formatDateTime
import vn.edu.student.fooddelivery.ui.shortId
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun OrderSummaryCard(
    order: DeliveryRequest,
    actionLabel: String? = null,
    actionLoading: Boolean = false,
    onAction: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Card(Modifier.fillMaxWidth().then(clickModifier)) {
        Column(Modifier.padding(Spacing.large)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        stringResource(R.string.order_number, shortId(order.id)),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        stringResource(R.string.created_time, formatDateTime(order.createdAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(order.status)
            }
            Spacer(Modifier.height(Spacing.large))
            Text(
                stringResource(R.string.item_reference, order.foodItemId),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(Spacing.medium))
            Text(stringResource(R.string.from_address), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(order.restaurantAddress, maxLines = 2)
            Spacer(Modifier.height(Spacing.small))
            Text(stringResource(R.string.to_address), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(order.destinationAddress, maxLines = 2)
            Spacer(Modifier.height(Spacing.medium))
            Text(
                stringResource(R.string.fee_value, formatCurrency(order.fee)),
                style = MaterialTheme.typography.titleSmall
            )
            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.height(Spacing.large))
                PrimaryButton(actionLabel, onAction, loading = actionLoading)
            }
        }
    }
}

package vn.edu.student.fooddelivery.client.createorder

import vn.edu.student.fooddelivery.ui.theme.Spacing
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.ui.components.DeliveryTopBar
import vn.edu.student.fooddelivery.ui.components.InfoRow
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.components.UiStateContent
import vn.edu.student.fooddelivery.ui.formatCurrency

@Composable
fun CreateOrderScreen(viewModel: CreateOrderViewModel, onBack: () -> Unit, onSuccess: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { DeliveryTopBar(stringResource(R.string.create_order_title), onBack = onBack) }) { padding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(padding),
            emptyMessage = stringResource(R.string.no_data),
            onRetry = viewModel::retry
        ) { data ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(Spacing.large)
            ) {
                Text(data.foodItem.name, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(Spacing.large))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.large)) {
                        Text(stringResource(R.string.pickup_address), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(data.restaurant.address)
                    }
                }
                Spacer(Modifier.height(Spacing.large))
                OutlinedTextField(
                    value = data.address,
                    onValueChange = viewModel::onAddressChange,
                    label = { Text(stringResource(R.string.destination_address)) },
                    placeholder = { Text(stringResource(R.string.destination_hint)) },
                    supportingText = { data.addressError?.let { Text(it) } },
                    isError = data.addressError != null,
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Spacing.xLarge))
                Text(stringResource(R.string.delivery_fee), style = MaterialTheme.typography.titleLarge)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Spacing.large)) {
                        InfoRow(stringResource(R.string.base_fee), formatCurrency(data.fee.baseFee))
                        Spacer(Modifier.height(Spacing.small))
                        InfoRow(stringResource(R.string.distance_fee), formatCurrency(data.fee.distanceFee))
                        Spacer(Modifier.height(Spacing.small))
                        InfoRow(stringResource(R.string.weight_surcharge), formatCurrency(data.fee.extraWeightFee))
                        Spacer(Modifier.height(Spacing.medium))
                        HorizontalDivider()
                        Spacer(Modifier.height(Spacing.medium))
                        InfoRow(stringResource(R.string.total_fee), formatCurrency(data.fee.total), emphasized = true)
                    }
                }
                Spacer(Modifier.height(Spacing.xLarge))
                PrimaryButton(
                    text = stringResource(R.string.confirm_order),
                    onClick = { viewModel.submit(onSuccess) },
                    enabled = data.address.isNotBlank() && data.addressError == null,
                    loading = data.isSubmitting
                )
            }
        }
    }
}

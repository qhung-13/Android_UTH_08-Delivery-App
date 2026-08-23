package vn.edu.student.fooddelivery.client.createorder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.components.UiStateContent

@Composable
fun CreateOrderScreen(
    viewModel: CreateOrderViewModel,
    onNavigateBackOrTracking: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val address by viewModel.address.collectAsState()
    val addressError by viewModel.addressError.collectAsState()
    val restaurantAddress by viewModel.restaurantAddress.collectAsState()
    val feePreview by viewModel.feePreview.collectAsState()

    UiStateContent(state = uiState) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.restaurant_address, restaurantAddress),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { viewModel.onAddressChange(it) },
                label = { Text(stringResource(R.string.destination_address)) },
                isError = addressError != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (addressError != null) {
                Text(
                    text = addressError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            feePreview?.let { fee ->
                Text(
                    text = stringResource(R.string.fee_preview, fee.toString()),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            PrimaryButton(
                text = stringResource(R.string.confirm_order),
                onClick = { viewModel.submitOrder(onSuccess = onNavigateBackOrTracking) },
                enabled = addressError == null && address.isNotBlank() && feePreview != null
            )
        }
    }
}
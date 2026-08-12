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
import vn.edu.student.fooddelivery.domain.util.UiState
import vn.edu.student.fooddelivery.ui.components.*

@Composable
fun CreateOrderScreen(
    viewModel: CreateOrderViewModel,
    onNavigateBackOrTracking: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val address by viewModel.address.collectAsState()
    val addressError by viewModel.addressError.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Error -> ErrorState(message = state.message)
        is UiState.Empty -> Unit
        is UiState.Success -> {
            Column(modifier = Modifier.padding(16.dp)) {
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

                Button(
                    onClick = { viewModel.submitOrder(onSuccess = onNavigateBackOrTracking) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = addressError == null && address.isNotBlank()
                ) {
                    Text(stringResource(R.string.confirm_order))
                }
            }
        }
    }
}
package vn.edu.student.fooddelivery.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.auth.AuthViewModel
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun LoginScreen(viewModel: AuthViewModel, onExistingAccounts: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf(Role.CLIENT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xLarge, vertical = Spacing.xxLarge),
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
        Text(
            stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(Spacing.xxLarge))
        Text(stringResource(R.string.login_title), style = MaterialTheme.typography.headlineSmall)
        Text(
            stringResource(R.string.login_supporting),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Spacing.xLarge))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; viewModel.clearError() },
            label = { Text(stringResource(R.string.full_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.medium))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; viewModel.clearError() },
            label = { Text(stringResource(R.string.phone)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.large))
        Text(stringResource(R.string.choose_role), style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            FilterChip(
                selected = role == Role.CLIENT,
                onClick = { role = Role.CLIENT },
                label = { Text(stringResource(R.string.client)) }
            )
            FilterChip(
                selected = role == Role.SHIPPER,
                onClick = { role = Role.SHIPPER },
                label = { Text(stringResource(R.string.shipper)) }
            )
        }
        state.error?.let { message ->
            Spacer(Modifier.height(Spacing.medium))
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(message, modifier = Modifier.padding(Spacing.medium), color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
        Spacer(Modifier.height(Spacing.xLarge))
        PrimaryButton(
            text = stringResource(R.string.register_continue),
            onClick = { viewModel.register(name, phone, role) },
            loading = state.isSubmitting
        )
        if (state.accounts.isNotEmpty()) {
            TextButton(onClick = onExistingAccounts, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.existing_accounts))
            }
        }
    }
}

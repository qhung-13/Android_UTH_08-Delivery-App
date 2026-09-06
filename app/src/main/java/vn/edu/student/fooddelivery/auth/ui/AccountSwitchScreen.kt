package vn.edu.student.fooddelivery.auth.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.student.fooddelivery.R
import vn.edu.student.fooddelivery.auth.AuthViewModel
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.model.User
import vn.edu.student.fooddelivery.ui.components.EmptyState
import vn.edu.student.fooddelivery.ui.components.PrimaryButton
import vn.edu.student.fooddelivery.ui.theme.Spacing

@Composable
fun AccountSwitchScreen(
    viewModel: AuthViewModel,
    onAccountSelected: (Role) -> Unit,
    onCreateNewAccount: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refreshAccounts() }

    Column(Modifier.fillMaxSize().padding(Spacing.xLarge)) {
        Text(stringResource(R.string.switch_account_title), style = MaterialTheme.typography.headlineSmall)
        Text(
            stringResource(R.string.switch_account_supporting),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Spacing.xLarge))
        if (state.accounts.isEmpty()) {
            EmptyState(
                message = stringResource(R.string.empty_accounts),
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                items(state.accounts, key = User::id) { account ->
                    AccountCard(
                        account = account,
                        busy = state.switchingAccountId == account.id,
                        enabled = state.switchingAccountId == null,
                        onClick = { viewModel.switchAccount(account.id, onAccountSelected) }
                    )
                }
            }
        }
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = Spacing.small))
        }
        PrimaryButton(text = stringResource(R.string.create_new_account), onClick = onCreateNewAccount)
        TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.logout))
        }
    }
}

@Composable
private fun AccountCard(account: User, busy: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(account.name, style = MaterialTheme.typography.titleMedium)
                Text(account.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    if (account.role == Role.CLIENT) stringResource(R.string.client)
                    else stringResource(R.string.shipper),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            if (busy) CircularProgressIndicator(Modifier.height(24.dp), strokeWidth = 2.dp)
        }
    }
}

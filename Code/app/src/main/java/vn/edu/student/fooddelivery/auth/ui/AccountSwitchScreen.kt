package vn.edu.student.fooddelivery.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import vn.edu.student.fooddelivery.auth.AuthViewModel
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.model.User
import vn.edu.student.fooddelivery.ui.components.PrimaryButton

@Composable
fun AccountSwitchScreen(
    viewModel: AuthViewModel,
    onAccountSelected: (Role) -> Unit,
    onCreateNewAccount: () -> Unit
) {
    val accounts by viewModel.allAccounts.collectAsState()

    // Mỗi lần vào màn này, load lại danh sách tài khoản mới nhất
    LaunchedEffect(Unit) {
        viewModel.refreshAccounts()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Chọn tài khoản", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (accounts.isEmpty()) {
            Text(text = "Chưa có tài khoản nào trên máy này")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(accounts) { account ->
                    AccountRow(
                        account = account,
                        onClick = {
                            viewModel.switchAccount(account.id)
                            onAccountSelected(account.role)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Đăng ký tài khoản mới",
            onClick = onCreateNewAccount
        )
    }
}

@Composable
private fun AccountRow(account: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = account.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${account.phone} · ${if (account.role == Role.CLIENT) "Client" else "Shipper"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
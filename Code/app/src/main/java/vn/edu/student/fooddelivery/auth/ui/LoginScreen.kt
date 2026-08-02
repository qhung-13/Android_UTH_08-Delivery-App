package vn.edu.student.fooddelivery.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.auth.AuthViewModel
import vn.edu.student.fooddelivery.domain.model.Role

/**
 * Màn hình đăng ký tài khoản (mock login, không có password).
 * Dùng rememberSaveable để giữ input khi xoay màn hình (configuration change).
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableStateOf(Role.CLIENT) }

    val error by viewModel.registerError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Đăng ký tài khoản")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ tên") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Chọn vai trò")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedRole == Role.CLIENT,
                    onClick = { selectedRole = Role.CLIENT },
                    label = { Text("Client") }
                )
                FilterChip(
                    selected = selectedRole == Role.SHIPPER,
                    onClick = { selectedRole = Role.SHIPPER },
                    label = { Text("Shipper") }
                )
            }
        }

        error?.let {
            Text(text = it)
        }

        Button(
            onClick = {
                viewModel.register(name, phone, selectedRole)
                onRegisterSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng ký & Tiếp tục")
        }
    }
}

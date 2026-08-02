package vn.edu.student.fooddelivery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.edu.student.fooddelivery.auth.AuthViewModel
import vn.edu.student.fooddelivery.auth.ui.LoginScreen
import vn.edu.student.fooddelivery.ui.theme.FoodDeliveryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodDeliveryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Lấy repository từ Application class (FoodDeliveryApp) đã khởi tạo sẵn
                    val app = application as FoodDeliveryApp

                    // AuthViewModel cần userRepository trong constructor,
                    // nên phải tự tạo Factory để Android biết cách khởi tạo nó
                    val authViewModel: AuthViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return AuthViewModel(app.userRepository) as T
                            }
                        }
                    )

                    // biến tạm để test: khi đăng ký xong, đổi màn hình hiển thị thông báo
                    var registered by remember { mutableStateOf(false) }

                    if (registered) {
                        Text(
                            text = "Đăng ký thành công! (màn Home sẽ nối vào đây sau)",
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        LoginScreen(
                            viewModel = authViewModel,
                            onRegisterSuccess = { registered = true }
                        )
                    }
                }
            }
        }
    }
}

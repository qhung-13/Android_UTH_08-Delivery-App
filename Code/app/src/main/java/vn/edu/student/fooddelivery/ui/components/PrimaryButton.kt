package vn.edu.student.fooddelivery.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Nút CTA chuẩn dùng chung toàn app (Đăng ký, Tạo đơn, Nhận đơn, Cập nhật trạng thái...).
 * Không tự viết Button() riêng ở từng màn hình.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(48.dp)
    ) {
        Text(text)
    }
}
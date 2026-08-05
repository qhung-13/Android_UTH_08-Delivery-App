package vn.edu.student.fooddelivery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.ui.theme.StatusAccepted
import vn.edu.student.fooddelivery.ui.theme.StatusCancelled
import vn.edu.student.fooddelivery.ui.theme.StatusDelivered
import vn.edu.student.fooddelivery.ui.theme.StatusInTransit
import vn.edu.student.fooddelivery.ui.theme.StatusPending
import vn.edu.student.fooddelivery.ui.theme.StatusPickedUp

/**
 * Badge hiển thị trạng thái đơn hàng, dùng chung ở mọi màn hình
 * (Tracking, History, Shipper list...) - không tự viết badge riêng.
 */
@Composable
fun StatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (color, label) = when (status) {
        OrderStatus.PENDING -> StatusPending to "Chờ xử lý"
        OrderStatus.ACCEPTED -> StatusAccepted to "Đã nhận"
        OrderStatus.PICKED_UP -> StatusPickedUp to "Đã lấy hàng"
        OrderStatus.IN_TRANSIT -> StatusInTransit to "Đang giao"
        OrderStatus.DELIVERED -> StatusDelivered to "Đã giao"
        OrderStatus.CANCELLED -> StatusCancelled to "Đã huỷ"
    }

    Text(
        text = label,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(color = color, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
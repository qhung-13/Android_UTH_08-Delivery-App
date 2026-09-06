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
import androidx.compose.ui.res.stringResource
import vn.edu.student.fooddelivery.R
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
        OrderStatus.PENDING -> StatusPending to stringResource(R.string.status_pending)
        OrderStatus.ACCEPTED -> StatusAccepted to stringResource(R.string.status_accepted)
        OrderStatus.PICKED_UP -> StatusPickedUp to stringResource(R.string.status_picked_up)
        OrderStatus.IN_TRANSIT -> StatusInTransit to stringResource(R.string.status_in_transit)
        OrderStatus.DELIVERED -> StatusDelivered to stringResource(R.string.status_delivered)
        OrderStatus.CANCELLED -> StatusCancelled to stringResource(R.string.status_cancelled)
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

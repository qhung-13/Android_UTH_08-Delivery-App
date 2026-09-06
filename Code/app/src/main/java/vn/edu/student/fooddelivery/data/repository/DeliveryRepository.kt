package vn.edu.student.fooddelivery.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vn.edu.student.fooddelivery.data.local.dao.DeliveryRequestDao
import vn.edu.student.fooddelivery.data.local.dao.UserDao
import vn.edu.student.fooddelivery.data.local.entity.DeliveryRequestEntity
import vn.edu.student.fooddelivery.data.local.toDomain
import vn.edu.student.fooddelivery.data.local.toEntity
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.model.Role
import vn.edu.student.fooddelivery.domain.model.StatusLog
import vn.edu.student.fooddelivery.domain.util.OrderStatusValidator
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching
import vn.edu.student.fooddelivery.domain.validation.InputValidator

interface DeliveryRepository {
    suspend fun createRequest(request: DeliveryRequest): Result<Unit>
    suspend fun cancelRequest(requestId: String): Result<Unit>
    fun getRequestsByClient(clientId: String): Flow<List<DeliveryRequest>>
    fun getPendingRequests(): Flow<List<DeliveryRequest>>
    fun getRequestsByShipper(shipperId: String): Flow<List<DeliveryRequest>>
    suspend fun acceptRequest(requestId: String, shipperId: String): Result<Unit>
    suspend fun updateStatus(requestId: String, newStatus: OrderStatus): Result<Unit>
    suspend fun getRequestById(requestId: String): Result<DeliveryRequest>
    suspend fun getStatusHistory(requestId: String): Result<List<StatusLog>>
}

class DeliveryRepositoryImpl(
    private val dao: DeliveryRequestDao,
    private val userDao: UserDao
) : DeliveryRepository {

    override suspend fun createRequest(request: DeliveryRequest): Result<Unit> = runSuspendCatching {
        require(request.id.isNotBlank()) { "Mã đơn hàng không hợp lệ" }
        require(request.foodItemId.isNotBlank()) { "Món ăn không hợp lệ" }
        require(InputValidator.isValidAddress(request.restaurantAddress)) { "Địa chỉ lấy hàng không hợp lệ" }
        require(InputValidator.isValidAddress(request.destinationAddress)) { "Địa chỉ nhận hàng không hợp lệ" }
        require(request.fee.isFinite() && request.fee >= 0) { "Phí giao hàng không hợp lệ" }
        require(request.status == OrderStatus.PENDING) { "Đơn mới phải ở trạng thái chờ nhận" }
        require(request.shipperId == null) { "Đơn mới chưa được gán Shipper" }
        require(request.statusHistory.isEmpty()) { "Lịch sử đơn mới do hệ thống khởi tạo" }
        require(request.createdAt > 0 && request.lastStatusUpdateAt >= request.createdAt) {
            "Thời gian đơn hàng không hợp lệ"
        }
        val client = userDao.getById(request.clientId) ?: error("Không tìm thấy tài khoản Client")
        require(client.role == Role.CLIENT.name) { "Chỉ Client mới được tạo đơn" }
        dao.insertWithInitialLog(request.toEntity())
    }

    override suspend fun cancelRequest(requestId: String): Result<Unit> =
        updateStatus(requestId, OrderStatus.CANCELLED)

    override fun getRequestsByClient(clientId: String): Flow<List<DeliveryRequest>> =
        dao.getByClient(clientId).withHistory()

    override fun getPendingRequests(): Flow<List<DeliveryRequest>> =
        dao.getPending().withHistory()

    override fun getRequestsByShipper(shipperId: String): Flow<List<DeliveryRequest>> =
        dao.getByShipper(shipperId).withHistory()

    override suspend fun acceptRequest(requestId: String, shipperId: String): Result<Unit> =
        runSuspendCatching {
            require(requestId.isNotBlank()) { "Mã đơn hàng không hợp lệ" }
            val shipper = userDao.getById(shipperId) ?: error("Không tìm thấy tài khoản Shipper")
            require(shipper.role == Role.SHIPPER.name) { "Chỉ Shipper mới được nhận đơn" }
            val current = dao.getById(requestId) ?: error("Không tìm thấy đơn hàng")
            val currentStatus = current.status.toOrderStatus()
            require(OrderStatusValidator.canTransition(currentStatus, OrderStatus.ACCEPTED)) {
                "Đơn đã được nhận hoặc không còn ở trạng thái chờ"
            }
            val changed = dao.transitionStatusWithLog(
                requestId = requestId,
                expectedStatus = currentStatus.name,
                newStatus = OrderStatus.ACCEPTED.name,
                timestamp = System.currentTimeMillis(),
                shipperId = shipperId
            )
            check(changed) { "Đơn vừa được Shipper khác nhận. Vui lòng tải lại danh sách" }
        }

    override suspend fun updateStatus(requestId: String, newStatus: OrderStatus): Result<Unit> =
        runSuspendCatching {
            require(requestId.isNotBlank()) { "Mã đơn hàng không hợp lệ" }
            val current = dao.getById(requestId) ?: error("Không tìm thấy đơn hàng")
            val currentStatus = current.status.toOrderStatus()
            require(OrderStatusValidator.canTransition(currentStatus, newStatus)) {
                "Không thể chuyển từ ${currentStatus.name} sang ${newStatus.name}"
            }
            require(newStatus == OrderStatus.CANCELLED || current.shipperId != null) {
                "Đơn chưa được Shipper nhận"
            }
            val changed = dao.transitionStatusWithLog(
                requestId = requestId,
                expectedStatus = currentStatus.name,
                newStatus = newStatus.name,
                timestamp = System.currentTimeMillis()
            )
            check(changed) { "Trạng thái đơn vừa thay đổi. Vui lòng tải lại" }
        }

    override suspend fun getRequestById(requestId: String): Result<DeliveryRequest> =
        runSuspendCatching {
            val entity = dao.getById(requestId) ?: error("Không tìm thấy đơn hàng")
            entity.toDomain(dao.getStatusHistory(requestId))
        }

    override suspend fun getStatusHistory(requestId: String): Result<List<StatusLog>> =
        runSuspendCatching { dao.getStatusHistory(requestId).map { it.toDomain() } }

    private fun Flow<List<DeliveryRequestEntity>>.withHistory(): Flow<List<DeliveryRequest>> =
        map { entities ->
            buildList {
                for (entity in entities) {
                    add(entity.toDomain(dao.getStatusHistory(entity.id)))
                }
            }
        }

    private fun String.toOrderStatus(): OrderStatus =
        try {
            OrderStatus.valueOf(this)
        } catch (_: IllegalArgumentException) {
            throw IllegalStateException("Trạng thái đơn hàng không hợp lệ")
        }
}

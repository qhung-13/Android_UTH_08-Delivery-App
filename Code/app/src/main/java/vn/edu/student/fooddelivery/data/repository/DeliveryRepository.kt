package vn.edu.student.fooddelivery.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vn.edu.student.fooddelivery.data.local.dao.DeliveryRequestDao
import vn.edu.student.fooddelivery.data.local.toDomain
import vn.edu.student.fooddelivery.data.local.toEntity
import vn.edu.student.fooddelivery.domain.model.DeliveryRequest
import vn.edu.student.fooddelivery.domain.model.OrderStatus
import vn.edu.student.fooddelivery.domain.model.StatusLog
import vn.edu.student.fooddelivery.domain.util.OrderStatusValidator

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
    private val dao: DeliveryRequestDao
) : DeliveryRepository {

    override suspend fun createRequest(request: DeliveryRequest): Result<Unit> = runCatching {
        dao.insertWithInitialLog(request.toEntity())
    }

    override suspend fun cancelRequest(requestId: String): Result<Unit> =
        updateStatus(requestId, OrderStatus.CANCELLED)

    override fun getRequestsByClient(clientId: String): Flow<List<DeliveryRequest>> =
        dao.getByClient(clientId).map { list -> list.map { it.toDomain() } }

    override fun getPendingRequests(): Flow<List<DeliveryRequest>> =
        dao.getPending().map { list -> list.map { it.toDomain() } }

    override fun getRequestsByShipper(shipperId: String): Flow<List<DeliveryRequest>> =
        dao.getByShipper(shipperId).map { list -> list.map { it.toDomain() } }

    override suspend fun acceptRequest(requestId: String, shipperId: String): Result<Unit> = runCatching {
        val current = dao.getById(requestId)
            ?: throw IllegalStateException("Không tìm thấy đơn hàng")
        val currentStatus = OrderStatus.valueOf(current.status)

        if (!OrderStatusValidator.canTransition(currentStatus, OrderStatus.ACCEPTED)) {
            throw IllegalStateException("Không thể nhận đơn ở trạng thái hiện tại")
        }

        dao.updateStatusWithLog(
            requestId = requestId,
            newStatus = OrderStatus.ACCEPTED.name,
            timestamp = System.currentTimeMillis(),
            shipperId = shipperId
        )
    }

    override suspend fun updateStatus(requestId: String, newStatus: OrderStatus): Result<Unit> = runCatching {
        val current = dao.getById(requestId)
            ?: throw IllegalStateException("Không tìm thấy đơn hàng")
        val currentStatus = OrderStatus.valueOf(current.status)

        if (!OrderStatusValidator.canTransition(currentStatus, newStatus)) {
            throw IllegalStateException(
                "Không thể chuyển trạng thái từ $currentStatus sang $newStatus"
            )
        }

        dao.updateStatusWithLog(
            requestId = requestId,
            newStatus = newStatus.name,
            timestamp = System.currentTimeMillis()
        )
    }

    override suspend fun getRequestById(requestId: String): Result<DeliveryRequest> = runCatching {
        dao.getById(requestId)?.toDomain()
            ?: throw NoSuchElementException("Không tìm thấy đơn hàng")
    }

    override suspend fun getStatusHistory(requestId: String): Result<List<StatusLog>> = runCatching {
        dao.getStatusHistory(requestId).map { it.toDomain() }
    }
}

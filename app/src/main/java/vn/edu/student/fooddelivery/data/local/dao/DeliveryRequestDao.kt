package vn.edu.student.fooddelivery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import vn.edu.student.fooddelivery.data.local.entity.DeliveryRequestEntity
import vn.edu.student.fooddelivery.data.local.entity.StatusLogEntity

@Dao
interface DeliveryRequestDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(request: DeliveryRequestEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStatusLog(log: StatusLogEntity)

    @Query(
        """
        UPDATE delivery_requests
        SET status = :newStatus,
            lastStatusUpdateAt = :timestamp,
            shipperId = COALESCE(:shipperId, shipperId)
        WHERE id = :requestId AND status = :expectedStatus
        """
    )
    suspend fun compareAndSetStatus(
        requestId: String,
        expectedStatus: String,
        newStatus: String,
        timestamp: Long,
        shipperId: String? = null
    ): Int

    @Query("SELECT * FROM delivery_requests WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getByClient(clientId: String): Flow<List<DeliveryRequestEntity>>

    @Query("SELECT * FROM delivery_requests WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPending(): Flow<List<DeliveryRequestEntity>>

    @Query("SELECT * FROM delivery_requests WHERE shipperId = :shipperId ORDER BY lastStatusUpdateAt DESC")
    fun getByShipper(shipperId: String): Flow<List<DeliveryRequestEntity>>

    @Query("SELECT * FROM delivery_requests WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DeliveryRequestEntity?

    @Query("SELECT * FROM status_logs WHERE deliveryRequestId = :requestId ORDER BY timestamp ASC")
    suspend fun getStatusHistory(requestId: String): List<StatusLogEntity>

    /**
     * Cập nhật status + ghi log CÙNG 1 transaction -> atomic,
     * tránh trường hợp update status thành công nhưng log lỗi (hoặc ngược lại).
     */
    @Transaction
    suspend fun transitionStatusWithLog(
        requestId: String,
        expectedStatus: String,
        newStatus: String,
        timestamp: Long,
        shipperId: String? = null
    ): Boolean {
        val changed = compareAndSetStatus(
            requestId = requestId,
            expectedStatus = expectedStatus,
            newStatus = newStatus,
            timestamp = timestamp,
            shipperId = shipperId
        )
        if (changed != 1) return false
        insertStatusLog(
            StatusLogEntity(
                deliveryRequestId = requestId,
                status = newStatus,
                timestamp = timestamp
            )
        )
        return true
    }

    /**
     * Insert đơn hàng mới + ghi log trạng thái ban đầu (PENDING) CÙNG 1 transaction.
     * Atomic — nếu insert đơn thành công mà ghi log lỗi thì huỷ cả hai, không để dữ liệu nửa vời.
     */
    @Transaction
    suspend fun insertWithInitialLog(request: DeliveryRequestEntity) {
        insert(request)
        insertStatusLog(
            StatusLogEntity(
                deliveryRequestId = request.id,
                status = request.status,
                timestamp = request.createdAt
            )
        )
    }
}

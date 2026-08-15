package vn.edu.student.fooddelivery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import vn.edu.student.fooddelivery.data.local.entity.DeliveryRequestEntity
import vn.edu.student.fooddelivery.data.local.entity.StatusLogEntity

@Dao
interface DeliveryRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: DeliveryRequestEntity)

    @Update
    suspend fun update(request: DeliveryRequestEntity)

    @Insert
    suspend fun insertStatusLog(log: StatusLogEntity)

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
    suspend fun updateStatusWithLog(
        requestId: String,
        newStatus: String,
        timestamp: Long,
        shipperId: String? = null
    ) {
        val current = getById(requestId) ?: return
        val updated = current.copy(
            status = newStatus,
            lastStatusUpdateAt = timestamp,
            shipperId = shipperId ?: current.shipperId
        )
        update(updated)
        insertStatusLog(
            StatusLogEntity(
                deliveryRequestId = requestId,
                status = newStatus,
                timestamp = timestamp
            )
        )
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

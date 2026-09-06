package vn.edu.student.fooddelivery.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vn.edu.student.fooddelivery.data.datastore.SessionManager
import vn.edu.student.fooddelivery.data.local.dao.UserDao
import vn.edu.student.fooddelivery.data.local.toDomain
import vn.edu.student.fooddelivery.data.local.toEntity
import vn.edu.student.fooddelivery.domain.model.User
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching
import vn.edu.student.fooddelivery.domain.validation.InputValidator

interface UserRepository {
    suspend fun createUser(user: User): Result<Unit>
    suspend fun getUserById(id: String): User?
    suspend fun getAllUsers(): List<User>
    fun getCurrentUser(): Flow<User?>
    suspend fun setCurrentUser(userId: String)
    suspend fun clearCurrentUser()
}

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : UserRepository {

    override suspend fun createUser(user: User): Result<Unit> = runSuspendCatching {
        require(user.id.isNotBlank()) { "Mã tài khoản không hợp lệ" }
        require(InputValidator.isValidName(user.name)) { "Tên không được để trống" }
        require(InputValidator.isValidPhone(user.phone)) { "Số điện thoại không hợp lệ" }
        userDao.insert(user.copy(name = user.name.trim(), phone = user.phone.trim()).toEntity())
    }

    override suspend fun getUserById(id: String): User? = userDao.getById(id)?.toDomain()

    override suspend fun getAllUsers(): List<User> =
        userDao.getAll().map { it.toDomain() }.sortedBy { it.name.lowercase() }

    override fun getCurrentUser(): Flow<User?> =
        sessionManager.currentUserIdFlow.map { userId ->
            if (userId == null) return@map null
            val user = userDao.getById(userId)?.toDomain()
            if (user == null) sessionManager.clearCurrentUser()
            user
        }

    override suspend fun setCurrentUser(userId: String) {
        requireNotNull(userDao.getById(userId)) { "Tài khoản không còn tồn tại" }
        sessionManager.setCurrentUserId(userId)
    }

    override suspend fun clearCurrentUser() = sessionManager.clearCurrentUser()
}

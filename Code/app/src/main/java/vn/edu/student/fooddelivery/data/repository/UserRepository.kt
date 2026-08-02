package vn.edu.student.fooddelivery.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vn.edu.student.fooddelivery.data.datastore.SessionManager
import vn.edu.student.fooddelivery.data.local.dao.UserDao
import vn.edu.student.fooddelivery.data.local.toDomain
import vn.edu.student.fooddelivery.data.local.toEntity
import vn.edu.student.fooddelivery.domain.model.User

interface UserRepository {
    suspend fun createUser(user: User): Result<Unit>
    suspend fun getUserById(id: String): User?
    fun getCurrentUser(): Flow<User?>
    suspend fun setCurrentUser(userId: String)
    suspend fun clearCurrentUser()
}

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : UserRepository {

    override suspend fun createUser(user: User): Result<Unit> = runCatching {
        userDao.insert(user.toEntity())
    }

    override suspend fun getUserById(id: String): User? =
        userDao.getById(id)?.toDomain()

    override fun getCurrentUser(): Flow<User?> =
        sessionManager.currentUserIdFlow.map { userId ->
            userId?.let { userDao.getById(it)?.toDomain() }
        }

    override suspend fun setCurrentUser(userId: String) {
        sessionManager.setCurrentUserId(userId)
    }

    override suspend fun clearCurrentUser() {
        sessionManager.clearCurrentUser()
    }
}

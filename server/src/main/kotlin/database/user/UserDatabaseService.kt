package database.user

interface UserDatabaseService {
    suspend fun insertNewUser(user: User): Boolean
    suspend fun updateUser(user: User): Boolean
    suspend fun getUserById(id: Int): User
    suspend fun getUserByUserId(userId: String): User
}
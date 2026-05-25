package database.user

interface UserDatabaseRepository {
    suspend fun insertNewUser(user: User): Boolean
    suspend fun updateUser(user: User): Boolean
    suspend fun getUserById(id: Int): User?
    suspend fun getUserByUserId(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
}
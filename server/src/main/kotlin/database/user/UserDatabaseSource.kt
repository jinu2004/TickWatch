package database.user

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

class UserDatabaseSource(private val database: R2dbcDatabase): UserDatabaseService {



    override suspend fun insertNewUser(user: User): Boolean {

        return suspendTransaction(database){
            UserTable.insert {
                it[userid] = user.userid
                it[username] = user.username
                it[email] = user.email
                it[password] = user.password
                it[salt] = user.salt
            }.insertedCount > 0

        }
    }

    override suspend fun updateUser(user: User): Boolean {
        return suspendTransaction(database) {
            UserTable.update({ UserTable.userid eq user.userid}){
                it[userid] = user.userid
                it[username] = user.username
                it[email] = user.email
                it[password] = user.password
                it[salt] = user.salt
            } > 0
        }
    }

    override suspend fun getUserById(id: Int): User? {
        return suspendTransaction(database) {
            UserTable.selectAll()
                .where { UserTable.id eq id }
                .map { row ->
                    User(
                        id = row[UserTable.id],
                        userid = row[UserTable.userid],
                        username = row[UserTable.username],
                        email = row[UserTable.email],
                        password = row[UserTable.password],
                        salt = row[UserTable.salt]
                        )
                }
                .singleOrNull()
        }
    }

    override suspend fun getUserByUserId(userId: String): User? {
        return suspendTransaction(database) {
            UserTable.selectAll()
                .where { UserTable.userid eq userId }
                .map { row ->
                    User(
                        id = row[UserTable.id],
                        userid = row[UserTable.userid],
                        username = row[UserTable.username],
                        email = row[UserTable.email],
                        password = row[UserTable.password],
                        salt = row[UserTable.salt]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return suspendTransaction(database) {
            UserTable.selectAll()
                .where { UserTable.email eq email }
                .map { row ->
                    User(
                        id = row[UserTable.id],
                        userid = row[UserTable.userid],
                        username = row[UserTable.username],
                        email = row[UserTable.email],
                        password = row[UserTable.password],
                        salt = row[UserTable.salt]
                    )
                }
                .singleOrNull()
        }
    }
}
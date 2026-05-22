package database.user

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table

const val MAX_CHAR_LENGTH = 128

object UserTable: Table("user") {
    val id = integer("id").autoIncrement()
    val userid = varchar("userid",MAX_CHAR_LENGTH)
    val username = varchar("username",MAX_CHAR_LENGTH)
    val email = varchar("email",MAX_CHAR_LENGTH)
    val password = varchar("password",MAX_CHAR_LENGTH)
    val salt = varchar("salt",MAX_CHAR_LENGTH)

    override val primaryKey = PrimaryKey(userid)
}


@Serializable
data class User(val id: Int? = null,
                val userid: String,
                val username: String,
                val email: String,
                val password : String,
                val salt: String)
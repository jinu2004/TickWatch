package database.projects

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.CurrentTimestamp
import org.jetbrains.exposed.v1.javatime.timestamp
import java.sql.Timestamp

const val MAX_CHAR_LENGTH = 128


object ProjectTable: Table("project_table"){
    val id = varchar("id",MAX_CHAR_LENGTH)
    val userId = varchar("userid",MAX_CHAR_LENGTH)
    val projectName = varchar("project_name",MAX_CHAR_LENGTH)
    val apiToken = varchar("token",MAX_CHAR_LENGTH)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}


data class Project(
    var id: String,
    var userId: String,
    var projectName: String,
    var apiToken: String,
    var createdAt: Timestamp
)
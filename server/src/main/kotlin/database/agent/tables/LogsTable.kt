package database.agent.tables

import agent.batch.enums.LogLevel
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object LogsTable :
    UUIDTable("logs") {

    val projectId =
        varchar("project_id",128)

    val serverId =
        varchar("server_id", 100)

    val batchId = uuid("batch_id")

    val level =
        enumerationByName("level", 20, LogLevel::class)

    val message =
        text("message")

    val timestamp =
        long("timestamp")

    init {
        index(false, projectId)
        index(false, level)
        index(false, timestamp)
    }
}
package database.agent.tables

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object EventsTable :
    UUIDTable("events") {

    const val MAX_CHAR_LENGTH = 128

    @OptIn(ExperimentalUuidApi::class)
    val projectId =
        varchar("project_id", MAX_CHAR_LENGTH)

    val serverId =
        varchar("server_id", MAX_CHAR_LENGTH)

    val batchId = uuid("batch_id")

    val event =
        varchar("event_name", MAX_CHAR_LENGTH)

    val timestamp =
        long("timestamp")

    val entityId =
        varchar("entity_id", MAX_CHAR_LENGTH)
            .nullable()

    val entityType =
        varchar("entity_type", MAX_CHAR_LENGTH)
            .nullable()

    val tags =
        text("tags")

    val data =
        text("data")

    init {
        index(false, projectId)
        index(false, event)
        index(false, timestamp)
        index(false, entityId)
    }
}
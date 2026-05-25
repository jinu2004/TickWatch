package database.agent.tables

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.java.javaUUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object RawBatchesTable :
    UUIDTable("raw_batches") {

    @OptIn(ExperimentalUuidApi::class)
    val projectId =
        javaUUID("project_id")

    val serverId =
        varchar("server_id", 100)

    val batchTimestamp =
        long("batch_timestamp")

    val receivedAt =
        long("received_at")
    val payload =
        text("payload")
    val metricCount =
        integer("metric_count")
    val eventCount =
        integer("event_count")
    val suspicionCount =
        integer("suspicion_count")
    val logCount =
        integer("log_count")
    val processingStatus =
        enumerationByName(
            "processing_status",
            20,
            ProcessingStatus::class
        )
    init {
        index(false, projectId)
        index(false, serverId)
        index(false, batchTimestamp)
        index(false, receivedAt)
    }
}
package database.agent.tables

import agent.batch.enums.AggregationType
import agent.batch.enums.MetricPriority
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object MetricsTable :
    UUIDTable("metrics") {

    val projectId = varchar("project_id", 100)

    val serverId = varchar("server_id", 100)

    val batchId = uuid("batch_id")

    val metric = varchar("metric", 100)

    val value = text("value")

    val timestamp = long("timestamp")

    val entityId = varchar("entity_id", 100).nullable()

    val entityType = varchar("entity_type", 50).nullable()

    val tags = text("tags")

    val meta = text("meta").nullable()

    val priority = enumerationByName("priority", 20, MetricPriority::class)

    val aggregationType = enumerationByName(
        "aggregation_type",
        20,
        AggregationType::class
    ).nullable()

    init {
        index(false, metric)
        index(false, timestamp)
        index(false, entityId)
        index(false, projectId)
    }
}
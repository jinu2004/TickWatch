package database.agent.sources

import agent.batch.MetricPayload
import database.agent.repository.MetricDataRepository
import database.agent.tables.MetricsTable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid


class MetricDataDataSource(private val db: R2dbcDatabase) : MetricDataRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun saveBatch(
        batchID: UUID,
        projectID: String,
        serverID: String,
        metrics: List<MetricPayload>
    ) {
        if (metrics.isEmpty()) return

        suspendTransaction(db){
            MetricsTable.batchInsert(metrics){ data ->
                this[MetricsTable.projectId] = projectID
                this[MetricsTable.serverId] = serverID
                this[MetricsTable.batchId] = batchID.toKotlinUuid()
                this[MetricsTable.metric] = data.metric
                this[MetricsTable.value] = data.value.toString()
                this[MetricsTable.timestamp] = data.timestamp
                this[MetricsTable.entityId] = data.entityId
                this[MetricsTable.entityType] = data.entityType
                this[MetricsTable.tags] = Json.encodeToString(data.tags)
                this[MetricsTable.priority] = data.priority
                this[MetricsTable.aggregationType] = data.aggregationType
                this[MetricsTable.meta] = data.meta?.let { Json.encodeToString(it) }
            }
        }
    }
}
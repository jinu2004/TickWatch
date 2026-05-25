package database.agent.sources

import agent.batch.AgentBatchRequest
import database.agent.repository.RawBatchDataRepository
import database.agent.tables.ProcessingStatus
import database.agent.tables.RawBatchesTable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

class RawBatchDataSource(private val db: R2dbcDatabase): RawBatchDataRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun save(id: String, request: AgentBatchRequest): UUID {
        return suspendTransaction(db){
            RawBatchesTable.insertAndGetId {
                it[projectId] = UUID.fromString(id)
                it[serverId] = request.serverId
                it[batchTimestamp] = request.timestamp
                it[receivedAt] = System.currentTimeMillis()
                it[payload] = Json.encodeToString(request)
                it[metricCount] = request.metrics.size
                it[eventCount] = request.events.size
                it[suspicionCount] = request.suspicionEvent.size
                it[logCount] = request.logs.size
                it[processingStatus] = ProcessingStatus.PENDING
            }.value
        }
    }

    override suspend fun updateStatus(
        batchID: UUID,
        status: ProcessingStatus
    ): Boolean {

        return suspendTransaction(db) {
            val updatedRows =
                RawBatchesTable.update(
                    { RawBatchesTable.id eq batchID }
                ) { it[processingStatus] = status }
            updatedRows > 0
        }
    }
}
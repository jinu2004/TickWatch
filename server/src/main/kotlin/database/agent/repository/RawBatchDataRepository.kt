package database.agent.repository

import agent.batch.AgentBatchRequest
import database.agent.tables.ProcessingStatus
import java.util.UUID

interface RawBatchDataRepository {
    suspend fun save(id: String,request: AgentBatchRequest): UUID
    suspend fun updateStatus(batchID: UUID, status: ProcessingStatus): Boolean
}
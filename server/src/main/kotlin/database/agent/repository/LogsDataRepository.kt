package database.agent.repository

import agent.batch.LogPayload
import agent.batch.MetricPayload
import java.util.UUID

interface LogsDataRepository {
    suspend fun saveBatch(batchID: UUID,
                          projectID: String,
                          serverID: String,
                          logs: List<LogPayload>)
}
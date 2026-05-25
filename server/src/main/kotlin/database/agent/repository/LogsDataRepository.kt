package database.agent.repository

import agent.batch.LogPayload
import agent.batch.MetricPayload
import dashboard.query.LogFilter
import java.util.UUID

interface LogsDataRepository {
    suspend fun saveBatch(batchID: UUID,
                          projectID: String,
                          serverID: String,
                          logs: List<LogPayload>)

    suspend fun getLogs(
        projectID: String,
        filter: LogFilter
    ): List<LogPayload>
}
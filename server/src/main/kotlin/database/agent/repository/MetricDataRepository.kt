package database.agent.repository

import agent.batch.MetricPayload
import java.util.UUID

interface MetricDataRepository {

    suspend fun saveBatch(batchID: UUID,
                          projectID: String,
                          serverID: String,
                          metrics: List<MetricPayload>)
}
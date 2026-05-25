package database.agent.repository

import agent.batch.MetricPayload
import dashboard.query.MetricFilter
import java.util.UUID

interface MetricDataRepository {

    suspend fun saveBatch(batchID: UUID,
                          projectID: String,
                          serverID: String,
                          metrics: List<MetricPayload>)
    suspend fun getMetrics(
        projectID: String,
        filter: MetricFilter
    ): List<MetricPayload>
}
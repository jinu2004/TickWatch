package database.agent.repository

import agent.batch.SuspicionEvent
import java.util.UUID

interface SuspicionDataRepository {
    suspend fun saveBatch(
        batchID: UUID,
        projectID: String,
        serverID: String,
        suspicion: List<SuspicionEvent>
    )

    suspend fun getLatest(projectID: String,limit: Int = 50): List<SuspicionEvent>
}
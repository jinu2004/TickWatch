package database.agent.repository

import agent.batch.EventPayload
import java.util.*

interface EventsDataRepository {
    suspend fun saveBatch(batchID: UUID,
                          projectID: String,
                          serverID: String,
                          events: List<EventPayload>)
}
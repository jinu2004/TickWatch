package database.agent.repository

import agent.batch.EventPayload
import dashboard.query.EventFilter
import java.util.*

interface EventsDataRepository {
    suspend fun saveBatch(batchID: UUID,
                          projectID: String,
                          serverID: String,
                          events: List<EventPayload>)

    suspend fun getEvents(
        projectID: String,
        filter: EventFilter
    ): List<EventPayload>
}
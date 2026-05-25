package database.agent.sources

import agent.batch.EventPayload
import database.agent.repository.EventsDataRepository
import database.agent.tables.EventsTable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

class EventsDataSource(
    private val db: R2dbcDatabase
) : EventsDataRepository {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun saveBatch(
        batchID: UUID,
        projectID: String,
        serverID: String,
        events: List<EventPayload>
    ) {

        if (events.isEmpty()) return

        suspendTransaction(db) {
            EventsTable.batchInsert(events) { data ->

                this[EventsTable.projectId] = projectID

                this[EventsTable.serverId] = serverID

                this[EventsTable.batchId] = batchID.toKotlinUuid()

                this[EventsTable.event] = data.event

                this[EventsTable.timestamp] = data.timestamp

                this[EventsTable.entityId] = data.entityId

                this[EventsTable.entityType] = data.entityType

                this[EventsTable.tags] = Json.encodeToString(data.tags)

                this[EventsTable.data] = Json.encodeToString(data.data)
            }
        }
    }
}
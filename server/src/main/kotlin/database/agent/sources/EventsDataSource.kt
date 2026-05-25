package database.agent.sources

import agent.batch.EventPayload
import dashboard.query.EventFilter
import database.agent.repository.EventsDataRepository
import database.agent.tables.EventsTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.selectAll
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

    override suspend fun getEvents(
        projectID: String,
        filter: EventFilter
    ): List<EventPayload> {
        return suspendTransaction(db) {

            val query =
                EventsTable
                    .selectAll()
                    .where { EventsTable.projectId eq projectID }

            filter.event?.let {
                query.andWhere {
                    EventsTable.event eq it
                }
            }

            filter.entityId?.let {
                query.andWhere {
                    EventsTable.entityId eq it
                }
            }

            filter.entityType?.let {
                query.andWhere {
                    EventsTable.entityType eq it
                }
            }

            filter.serverId?.let {
                query.andWhere {
                    EventsTable.serverId eq it
                }
            }

            filter.fromTimestamp?.let {
                query.andWhere {
                    EventsTable.timestamp greaterEq  it
                }
            }

            filter.toTimestamp?.let {
                query.andWhere {
                    EventsTable.timestamp lessEq  it
                }
            }

            query
                .orderBy(
                    EventsTable.timestamp,
                    SortOrder.DESC
                )
                .limit(filter.limit)
                .offset(filter.offset)
                .map { row ->

                    EventPayload(
                        event = row[EventsTable.event],
                        timestamp = row[EventsTable.timestamp],
                        entityId = row[EventsTable.entityId],
                        entityType = row[EventsTable.entityType],
                        tags = Json.decodeFromString(row[EventsTable.tags]),
                        data = Json.decodeFromString(row[EventsTable.data])
                    )
                }.toList()
        }
    }
}
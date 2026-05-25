package database.agent.sources



import agent.batch.SuspicionEvent
import database.agent.repository.SuspicionDataRepository
import database.agent.tables.SuspicionTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

class SuspicionDataSource(
    private val db: R2dbcDatabase
) : SuspicionDataRepository {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun saveBatch(
        batchID: UUID,
        projectID: String,
        serverID: String,
        suspicion: List<SuspicionEvent>
    ) {

        if (suspicion.isEmpty()) return

        suspendTransaction(db) {

            SuspicionTable.batchInsert(suspicion) { data ->

                this[SuspicionTable.projectId] = projectID
                this[SuspicionTable.serverId] = serverID
                this[SuspicionTable.batchId] = batchID.toKotlinUuid()

                this[SuspicionTable.eventId] = data.id
                this[SuspicionTable.entityId] = data.entityId
                this[SuspicionTable.entityType] = data.entityType

                this[SuspicionTable.ruleName] = data.ruleName
                this[SuspicionTable.reason] = data.reason

                this[SuspicionTable.value] = data.value
                this[SuspicionTable.threshold] = data.threshold
                this[SuspicionTable.severity] = data.severity

                this[SuspicionTable.metadata] =
                    Json.encodeToString(data.metadata)

                this[SuspicionTable.timestamp] = data.timestamp
                this[SuspicionTable.resolved] = false
            }
        }
    }

    override suspend fun getLatest(
        projectID: String,
        limit: Int
    ): List<SuspicionEvent> {
        return suspendTransaction(db){
            SuspicionTable.selectAll().where{
                SuspicionTable.projectId eq projectID
            }.orderBy(SuspicionTable.timestamp, SortOrder.DESC)
                .limit(limit).map {row ->
                    SuspicionEvent(
                        id = row[SuspicionTable.id].toString(),
                        entityId = row[SuspicionTable.entityId],
                        entityType = row[SuspicionTable.entityType],
                        ruleName = row[SuspicionTable.ruleName],
                        reason = row[SuspicionTable.reason],
                        value = row[SuspicionTable.value],
                        threshold = row[SuspicionTable.threshold],
                        severity = row[SuspicionTable.severity],
                        metadata = Json.decodeFromString(row[SuspicionTable.metadata]),
                        timestamp = row[SuspicionTable.timestamp]
                    )
                }.toList()
        }
    }
}
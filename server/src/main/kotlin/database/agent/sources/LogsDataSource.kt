package database.agent.sources

import agent.batch.LogPayload
import database.agent.repository.LogsDataRepository
import database.agent.tables.LogsTable
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

class LogsDataSource(
    private val db: R2dbcDatabase
) : LogsDataRepository {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun saveBatch(
        batchID: UUID,
        projectID: String,
        serverID: String,
        logs: List<LogPayload>
    ) {

        if (logs.isEmpty()) return

        suspendTransaction(db) {

            LogsTable.batchInsert(logs) { data ->

                this[LogsTable.projectId] = projectID

                this[LogsTable.serverId] = serverID

                this[LogsTable.batchId] = batchID.toKotlinUuid()

                this[LogsTable.level] = data.level

                this[LogsTable.message] = data.message

                this[LogsTable.timestamp] = data.timestamp
            }
        }
    }
}
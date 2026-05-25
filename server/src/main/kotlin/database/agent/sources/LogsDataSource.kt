package database.agent.sources

import agent.batch.LogPayload
import dashboard.query.LogFilter
import database.agent.repository.LogsDataRepository
import database.agent.tables.LogsTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.selectAll
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

    override suspend fun getLogs(
        projectID: String,
        filter: LogFilter
    ): List<LogPayload> {

        return suspendTransaction(db) {

            val query =
                LogsTable
                    .selectAll()
                    .where { LogsTable.projectId eq projectID }

            filter.level?.let {
                query.andWhere {
                    LogsTable.level eq it
                }
            }

            filter.serverId?.let {
                query.andWhere {
                    LogsTable.serverId eq it
                }
            }

            filter.search?.let {
                query.andWhere {
                    LogsTable.message like
                            "%$it%"
                }
            }

            filter.fromTimestamp?.let {
                query.andWhere {
                    LogsTable.timestamp greaterEq  it
                }
            }

            filter.toTimestamp?.let {
                query.andWhere {
                    LogsTable.timestamp lessEq  it
                }
            }

            query
                .orderBy(
                    LogsTable.timestamp,
                    SortOrder.DESC
                )
                .limit(filter.limit)
                .offset(filter.offset)
                .map { row ->
                    LogPayload(
                        level = row[LogsTable.level],
                        message = row[LogsTable.message],
                        timestamp = row[LogsTable.timestamp]
                    )
                }.toList()
        }
    }
}
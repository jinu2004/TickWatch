package database.agent.sources

import agent.batch.MetricPayload
import agent.query.MetricFilter
import database.agent.repository.MetricDataRepository
import database.agent.tables.MetricsTable
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


class MetricDataDataSource(private val db: R2dbcDatabase) : MetricDataRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun saveBatch(
        batchID: UUID,
        projectID: String,
        serverID: String,
        metrics: List<MetricPayload>
    ) {
        if (metrics.isEmpty()) return

        suspendTransaction(db) {
            MetricsTable.batchInsert(metrics) { data ->
                this[MetricsTable.projectId] = projectID
                this[MetricsTable.serverId] = serverID
                this[MetricsTable.batchId] = batchID.toKotlinUuid()
                this[MetricsTable.metric] = data.metric
                this[MetricsTable.value] = data.value.toString()
                this[MetricsTable.timestamp] = data.timestamp
                this[MetricsTable.entityId] = data.entityId
                this[MetricsTable.entityType] = data.entityType
                this[MetricsTable.tags] = Json.encodeToString(data.tags)
                this[MetricsTable.priority] = data.priority
                this[MetricsTable.aggregationType] = data.aggregationType
                this[MetricsTable.meta] = data.meta?.let { Json.encodeToString(it) }
            }
        }
    }

    override suspend fun getMetrics(projectID: String, filter: MetricFilter): List<MetricPayload> {
        return suspendTransaction(db) {

            val query =
                MetricsTable.selectAll()
                    .where {
                        MetricsTable.projectId eq projectID
                    }
            filter.metric?.let {
                query.andWhere {
                    MetricsTable.metric eq it
                }
            }

            filter.entityId?.let {
                query.andWhere {
                    MetricsTable.entityId eq it
                }
            }

            filter.entityType?.let {
                query.andWhere {
                    MetricsTable.entityType eq it
                }
            }

            filter.serverId?.let {
                query.andWhere {
                    MetricsTable.serverId eq it
                }
            }

            filter.fromTimestamp?.let {
                query.andWhere {
                    MetricsTable.timestamp greaterEq it
                }
            }

            filter.toTimestamp?.let {
                query.andWhere {
                    MetricsTable.timestamp lessEq it
                }
            }
            filter.aggregationType?.let {
                query.andWhere {
                    MetricsTable.aggregationType eq it
                }
            }

            query
                .orderBy(
                    MetricsTable.timestamp,
                    SortOrder.ASC
                )
                .limit(filter.limit)
                .map { row ->

                    MetricPayload(
                        metric = row[MetricsTable.metric],
                        value = Json.parseToJsonElement(row[MetricsTable.value]),
                        timestamp = row[MetricsTable.timestamp],
                        entityId = row[MetricsTable.entityId],
                        entityType = row[MetricsTable.entityType],
                        tags = Json.decodeFromString(row[MetricsTable.tags]),
                        priority = row[MetricsTable.priority],
                        meta = row[MetricsTable.meta]?.let { Json.decodeFromString(it) },
                        aggregationType = row[MetricsTable.aggregationType]
                    )
                }.toList()


        }
    }
}

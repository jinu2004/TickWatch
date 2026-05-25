package dashboard.query

import agent.batch.enums.AggregationType
import kotlinx.serialization.Serializable

@Serializable
data class MetricFilter(
    val metric: String? = null,
    val entityId: String? = null,
    val entityType: String? = null,
    val serverId: String? = null,
    val fromTimestamp: Long? = null,
    val toTimestamp: Long? = null,
    val aggregationType: AggregationType? = null,
    val limit: Int = 100
)
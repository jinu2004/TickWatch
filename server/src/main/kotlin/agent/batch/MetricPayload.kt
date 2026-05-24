package agent.batch

import agent.batch.enums.AggregationType
import agent.batch.enums.MetricPriority
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MetricPayload(
    val metric: String,
    val value: JsonElement,
    val timestamp: Long,
    val entityId: String? = null,
    val entityType: String? = null,
    val tags: Map<String, String> = emptyMap(),
    val priority: MetricPriority =
        MetricPriority.NORMAL,
    val meta: MetricMeta? = null,
    val aggregationType: AggregationType?
)